package com.bjpowernode.crm.commons.utils;

import org.apache.logging.log4j.util.Strings;

public class testMD5 {
    public static void main(String args[]){
        String md5Str=MD5Util.getMD5("ls");
        System.out.println(md5Str);
    }
}
