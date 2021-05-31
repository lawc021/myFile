package com.bjpowernode.web.service;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.commmon.CommonUtil;
import com.bjpowernode.contants.RedisKey;
import com.bjpowernode.web.config.SmsConfig;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

//短信发送处理
@Service
public class SmsService {

    @Resource
    private SmsConfig smsConfig;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 发送短信
     * boolean true:发送成功,false：发送失败
     */
    public boolean sendSmsCode(String phone,StringBuilder builder){
        boolean result  = false;
        //短信内容  【创信】你的验证码是：%s，3分钟内有效！
        String codeValue = CommonUtil.random(4);
        String smsText = String.format(smsConfig.getContent(), codeValue );

        //调用 创信的接口，使用HttpClient
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //https://way.jd.com/chuangxin/dxjk?mobile=13568813957&content=【创信】你的验证码是：5873，3分钟内有效！&appkey=您申请的APPKEY
        String url = smsConfig.getUrl()+"?mobile="+phone
                        +"&content="+smsText
                        +"&appkey="+smsConfig.getAppkey();
        HttpGet  httpGet = new HttpGet(url);

        //发起请求
        try {
            //CloseableHttpResponse response = httpClient.execute(httpGet);
            //if( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                //String json  = EntityUtils.toString(response.getEntity());
                String json = "{\"code\":\"10000\",\"charge\":false,\"remain\":0,\"msg\":\"查询成功\",\"result\":{\"ReturnStatus\":\"Success\",\"Message\":\"ok\",\"RemainPoint\":1219176,\"TaskID\":68065721,\"SuccessCounts\":1},\"requestId\":\"ad810f024ff444e3933c387deb735b08\"}";
                if( json != null ){
                    // {"code":"10000","charge":false,"remain":0,"msg":"查询成功","result":{"ReturnStatus":"Success","Message":"ok","RemainPoint":1219176,"TaskID":68065721,"SuccessCounts":1},"requestId":"ad810f024ff444e3933c387deb735b08"}
                    System.out.println("json===="+json);
                    JSONObject obj = JSONObject.parseObject(json);
                    if( "10000".equals(obj.getString("code"))){
                        //获取result ， ReturnStatus
                        String returnStatus = obj.getJSONObject("result").getString("ReturnStatus");
                        if("Success".equals(returnStatus)){
                            result = true;
                            builder.append(codeValue);
                        }
                    }
                }
            //}

            System.out.println("=========短信的验证码的内容===================="+codeValue);
        } catch (Exception e) {
            e.printStackTrace();
            result =false;
        }

        return  result;
    }



    //判断验证码是否有效
    public boolean matchCode(String phone,String code){
        boolean result = false;
        if( code != null && code.length() == 4){
            //从redis获取数据
            Object redisCode = redisTemplate.opsForValue().get(RedisKey.APP_REGISTER_SMS_CODE+phone);
            if( redisCode != null){
                result = redisCode.toString().equals(code);
            }
        }
        return result;
    }

}
