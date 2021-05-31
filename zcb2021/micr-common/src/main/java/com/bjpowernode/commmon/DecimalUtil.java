package com.bjpowernode.commmon;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DecimalUtil {

    // n1 >= n2
    public static boolean ge(BigDecimal n1, BigDecimal n2){
        return n1.compareTo(n2) >=0 ;
    }

    // n1 <= n2
    public static boolean le(BigDecimal n1, BigDecimal n2){
        return n1.compareTo(n2) <=0 ;
    }

    // n1 == n2
    public static boolean eq(BigDecimal n1, BigDecimal n2){
        return n1.compareTo(n2) ==0 ;
    }


    // 乘
    public static BigDecimal multiply(BigDecimal n1, BigDecimal n2){
        return n1.multiply(n2).stripTrailingZeros();
    }

    // 除
    public static BigDecimal divide(BigDecimal n1, BigDecimal n2){
        return n1.divide(n2, RoundingMode.HALF_UP).stripTrailingZeros();
    }
}
