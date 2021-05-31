package com.bjpowernode.commmon;

public class PageUtil {

    public static int defaultPageNo(Integer pageNo){
        if (pageNo == null || pageNo.intValue() < 1 ){
            pageNo = 1;
        }
        return pageNo;
    }


    public static int defaultPageSize(Integer pageSize){
        if( pageSize == null || pageSize.intValue() < 1 ){
            pageSize = 9;
        }
        return pageSize;
    }
}
