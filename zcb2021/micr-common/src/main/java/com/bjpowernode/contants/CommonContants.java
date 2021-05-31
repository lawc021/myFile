package com.bjpowernode.contants;

public class CommonContants {

    //定义产品类型
    //新手宝 0
    public static final Integer LAON_PRODUCT_TYPE_XINSHOUBAO_0=0;
    //优选  1
    public static final Integer LAON_PRODUCT_TYPE_YOUXUAN_1=1;
    //散标  2
    public static final Integer LAON_PRODUCT_TYPE_SANBIAO_2=2;

    // session中的user的key
    public static final String  SESSION_USER_KEY="LICAI_USER";


    //产品的状态  未满标 0
    public static final Integer LOAN_STATUS_WEIMANBIAO = 0;
    //满标 1
    public static final Integer LOAN_STATUS_MANBIAO = 1;
    //满标已生成收益计划  2
    public static final Integer LOAN_STATUS_MANBIAO_INCOME = 2;


    //收益的状态 income_status
    //生成收益计划
    public static final Integer INCOME_STATUS_PLAN = 0 ;
    //返还收益
    public static final Integer INCOME_STATUS_BACK = 1 ;


    //充值的状态
    //充值中
    public static final Integer RECHARGE_STATUS_PROCESSING = 0;
    //充值失败
    public static final Integer RECHARGE_STATUS_SUCCESS = 1;
    //充值成功
    public static final Integer RECHARGE_STATUS_FAILURE = 2;



}
