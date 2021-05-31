package com.bjpowernode.micrpay.service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.bjpowernode.contants.CommonContants;
import com.bjpowernode.contants.RedisKey;
import com.bjpowernode.licai.model.RechargeRecord;
import com.bjpowernode.licai.service.RechargeService;
import com.bjpowernode.micrpay.config.AlipayConfig;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Service
public class AlipayService {

    @Resource
    private AlipayConfig alipayConfig;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @DubboReference(interfaceClass = RechargeService.class,version = "1.0")
    private RechargeService rechargeService;

    public String alipayPagePay(Integer uid,BigDecimal money,String channel){
        //创建数据库的记录
        String form = "";
        RechargeRecord record = new RechargeRecord();
        record.setChannel(channel);
        record.setRechargeDesc("支付宝充值");
        record.setRechargeMoney(money);
        record.setRechargeNo(outTrandeNo()); //商户订单号
        record.setRechargeStatus(CommonContants.RECHARGE_STATUS_PROCESSING);
        record.setRechargeTime(new Date());
        record.setUid(uid);

        int rows  = rechargeService.addRecharge(record);
        if( rows > 0 ){
            System.out.println("out_trade_no==="+record.getRechargeNo());
            form =  alipayPlaceOrder(uid, channel,money,record.getRechargeNo());

            if( form != null && form.length() > 0){
                //把订单号和充值时间，存放到redis 的zset类型
                stringRedisTemplate.opsForZSet().add(
                        RedisKey.ALIPAY_ALL_TRANDE_NO,
                        record.getRechargeNo(),
                        record.getRechargeTime().getTime() );

            }
        }
        return form;

    }
    //下单
    public String alipayPlaceOrder(Integer uid, String channel, BigDecimal money,String outTradeNo){
        String form = "";
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig.getGatewayUrl(),
                alipayConfig.getApp_id(), alipayConfig.getMerchant_private_key(),
                "json", alipayConfig.getCharset(),
                alipayConfig.getAlipay_public_key(),
                alipayConfig.getSign_type());

        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(alipayConfig.getReturn_url());
        alipayRequest.setNotifyUrl(alipayConfig.getNotify_url());

        //商户订单号，商户网站订单系统中唯一订单号，必填
        //使用时间组合
        String out_trade_no = outTradeNo;
        //付款金额，必填
        String total_amount =  money.toString();
        //订单名称，必填
        String subject = "phone";
        //商品描述，可空
        String body = "powernode";

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        //请求
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
            form = "";
        }

        return form;//支付宝返回的表单
    }


    //处理支付宝的notify通知
    public void alipyNotify(Map<String,String> params) {
        try {
            boolean signVerified = AlipaySignature.rsaCheckV1(params,
                    alipayConfig.getAlipay_public_key(),
                    alipayConfig.getCharset(),
                    alipayConfig.getSign_type()); //调用SDK验证签名
            if (signVerified) {//验证成功
                //商户订单号
                String out_trade_no = params.get("out_trade_no");
                //支付宝交易号 ,暂时没有数据， 可以把他存到数据库
                String trade_no = params.get("trade_no");
                //交易状态
                String trade_status = params.get("trade_status");
                //订单金额
                String total_amount = params.get("total_amount");
                //appid
                String app_id = params.get("app_id");

                /**
                 * 1. 根据out_trade_no 查询数据库， 1）看订单是否存在，2）看订单是否处理过
                 * 2. 如果充值成功的， 更新用户资金账户的余额，同时更新充值记录的状态为 1 （充值成功）
                 * 3. 如果充值失败， 更新充值记录的状态为 2 （充值失败）
                 */
                if ( alipayConfig.getApp_id().equals(app_id)) {
                   rechargeService.doAlipayNotify(out_trade_no,trade_status,total_amount);
                   //删除 ZSet中已经处理过的订单号。
                   stringRedisTemplate.opsForZSet().remove(RedisKey.ALIPAY_ALL_TRANDE_NO,out_trade_no);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
      }




    //处理支付宝的查询接口
    public void alipyQuery(){
        //从redis获取没有处理的订单记录
        //TypedTuple表示ZSet它的 value 和score
        Set<ZSetOperations.TypedTuple<String>> tuples = stringRedisTemplate.opsForZSet().rangeWithScores(RedisKey.ALIPAY_ALL_TRANDE_NO, 0, -1);

        tuples.stream().forEach( t->{
            String tradeNo =  t.getValue();
            Double time = t.getScore();
            //充值时间比当前时间大于10分钟的。
            Date before10Min = DateUtils.addMinutes(new Date(),-10);

            if( time < before10Min.getTime()){
                //超过十分钟的。 调用支付宝的查询接口
                invokeAlipayQuery(tradeNo);
            }
        });
    }


    //查询接口
    public void invokeAlipayQuery(String outTradeNo){
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig.getGatewayUrl(),
                alipayConfig.getApp_id(), alipayConfig.getMerchant_private_key(),
                "json", alipayConfig.getCharset(),
                alipayConfig.getAlipay_public_key(),
                alipayConfig.getSign_type());
        //设置请求参数
        AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ outTradeNo +"\"}");

        try{
            AlipayTradeQueryResponse response = alipayClient.execute(alipayRequest);
            if( response.isSuccess() ) {//code = 10000
                //处理订单
                String json = response.getBody();
                JSONObject obj = JSONObject.parseObject(json).getJSONObject("alipay_trade_query_response");
                String out_trade_no = obj.getString("out_trade_no");
                String trade_status = obj.getString("trade_status");
                String total_amount = obj.getString("total_amount");
                rechargeService.doAlipayNotify(out_trade_no,trade_status,total_amount);
                stringRedisTemplate.opsForZSet().remove(RedisKey.ALIPAY_ALL_TRANDE_NO,out_trade_no);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //生成商户订单号
    private String outTrandeNo(){
       //20210507152930123
       String str =  DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")
               + stringRedisTemplate.opsForValue().increment(RedisKey.ALIPAY_OUT_TRANDE_NO);

       return str;
    }
}
