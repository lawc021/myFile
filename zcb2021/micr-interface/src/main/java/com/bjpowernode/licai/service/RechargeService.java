package com.bjpowernode.licai.service;

import com.bjpowernode.licai.model.RechargeRecord;
import com.bjpowernode.licai.model.mix.UserRechargeInfo;

import java.util.List;

//充值的服务
public interface RechargeService {

    //获取用户最近的充值记录
    List<UserRechargeInfo> queryRecentlyUserRechargeInfo(Integer uid,
                                                         Integer pageNo,
                                                         Integer pageSize);


    //创建充值记录
    int addRecharge(RechargeRecord record);


    //处理支付宝的notify
    int doAlipayNotify(String outTradeNo,String tradeStatus,String totalAmount);
}
