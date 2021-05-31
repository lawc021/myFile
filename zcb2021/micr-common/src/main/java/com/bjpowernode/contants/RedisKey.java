package com.bjpowernode.contants;

public class RedisKey {

    //redis key 自己的命名规则， 类别:子类别:项目
    public static final String APP_REGISTER_USER = "INDEX:USER:REGITER";
    //注册时，短信的验证码的key REGISTER:SMSCODE:138000000
    public static final String APP_REGISTER_SMS_CODE = "REGISTER:SMSCODE:";
    //投资排行榜
    public static final String BID_INVEST_TOP = "BID:INVEST:TOP";
    //商户订单号，产生的唯一值的部分
    public static final String ALIPAY_OUT_TRANDE_NO = "PAY:ALIPAY:TRADENO" ;
    public static final String ALIPAY_ALL_TRANDE_NO = "PAY:ALIAPY:ALL";
}
