package com.bjpowernode.commmon;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class CommonUtil {

    //产品类型 0新手宝，1优选产品，2散标产品
    public static boolean checkProductType(Integer productType){
        boolean result = false;
        if( productType != null ){
            if( productType == 0 || productType == 1 || productType == 2){
                result = true;
            }
        }
        return result;
    }

    //判断手机号格式
    public static boolean checkPhone(String phone){
        boolean result = false;
        if( phone != null && phone.length() == 11 ){
            result = Pattern.matches("^1[1-9]\\d{9}$",phone);
        }
        return result;
    }

    //获取指定位数的随机数
    public static String random(int len){
        StringBuilder builder = new StringBuilder("");
        ThreadLocalRandom  t  = ThreadLocalRandom.current();
        for(int i=0;i<len;i++){
            builder.append( t.nextInt(10));
        }
        return builder.toString();
    }
}
