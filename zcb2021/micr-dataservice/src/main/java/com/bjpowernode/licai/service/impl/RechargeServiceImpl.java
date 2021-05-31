package com.bjpowernode.licai.service.impl;

import com.bjpowernode.commmon.DecimalUtil;
import com.bjpowernode.commmon.PageUtil;
import com.bjpowernode.contants.CommonContants;
import com.bjpowernode.licai.mapper.FinanceAccountMapper;
import com.bjpowernode.licai.mapper.RechargeRecordMapper;
import com.bjpowernode.licai.model.FinanceAccount;
import com.bjpowernode.licai.model.RechargeRecord;
import com.bjpowernode.licai.model.mix.UserRechargeInfo;
import com.bjpowernode.licai.service.RechargeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@DubboService(interfaceClass = RechargeService.class,version = "1.0")
public class RechargeServiceImpl implements RechargeService {


    @Resource
    private RechargeRecordMapper rechargeRecordMapper;

    @Resource
    private FinanceAccountMapper financeAccountMapper;

    //获取用户最近的充值记录
    @Override
    public List<UserRechargeInfo> queryRecentlyUserRechargeInfo(Integer uid,
                                                                Integer pageNo,
                                                                Integer pageSize) {
        List<UserRechargeInfo> list = new ArrayList<>();
        if( uid != null && uid > 0){
            pageNo = PageUtil.defaultPageNo(pageNo);
            pageSize = PageUtil.defaultPageSize(pageSize);
            int offset = (pageNo - 1 ) * pageSize;
            list = rechargeRecordMapper.selectPageRechrageInfo(uid,offset,pageSize);
        }
        return list;
    }

    //创建充值记录
    @Override
    public int addRecharge(RechargeRecord record) {
        int rows = rechargeRecordMapper.insertSelective(record);
        return rows;
    }

    /**
     *
     * @param outTradeNo  商家订单号
     * @param tradeStatus 支付宝的通知的充值结果
     * @param totalAmount 支付宝的通知的支付金额
     * @return int：0：未知； 1:充值成功， 2 充值失败， 3 金额不一样 4.资金账户不存在 ， 5.记录不存在或者已经处理
     */
    @Transactional
    @Override
    public int doAlipayNotify(String outTradeNo, String tradeStatus, String totalAmount) {
        int result =  0;
        int rows = 0;
        //1.查询订单是否存在
        RechargeRecord dbRecord = rechargeRecordMapper.selectByRechargeNo(outTradeNo);
        //订单存在并且是充值中的状态 ，才需要处理
        if( dbRecord != null && dbRecord.getRechargeStatus() == CommonContants.RECHARGE_STATUS_PROCESSING){
            //检查金额和你的库中记录是否一致。
            if( DecimalUtil.eq( dbRecord.getRechargeMoney() , new BigDecimal(totalAmount)) ){
                //判断充值的结果
                if("TRADE_SUCCESS".equals(tradeStatus)){
                    //充值成功 ： 1 更新资金余额， 2 修改充值的状态为1（成功）
                    FinanceAccount account = financeAccountMapper.selectUidForUpdate(dbRecord.getUid());
                    if (account != null) {
                       // 更新资金余额
                       rows =  financeAccountMapper.updateAvaiableMoneyRecharge(dbRecord.getUid(),dbRecord.getRechargeMoney());
                       if( rows < 1){
                           throw new RuntimeException("充值处理支付宝异步通知：更新资金余额失败");
                       }
                       //修改充值的状态为1（成功）
                       rows = rechargeRecordMapper.updateRechargeStatus(dbRecord.getId(),CommonContants.RECHARGE_STATUS_SUCCESS);
                       if( rows < 1){
                           throw new RuntimeException("充值处理支付宝异步通知：更新充值记录状态为成功时失败");
                       }
                       result = 1;
                    } else {
                        result = 4;
                    }
                } else {
                    //充值失败
                    rows = rechargeRecordMapper.updateRechargeStatus(dbRecord.getId(),CommonContants.RECHARGE_STATUS_FAILURE);
                    if( rows < 1){
                        throw new RuntimeException("充值处理支付宝异步通知：更新充值记录状态为失败时失败");
                    }
                    result = 2;
                }
            } else {
                //金额不一样
                result = 3;
            }
        } else {
            //记录不存在或者已经处理
            result = 5;
        }

        return result;
    }
}
