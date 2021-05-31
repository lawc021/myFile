package com.bjpowernode.web.service;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.commmon.HttpClientUtils;
import com.bjpowernode.licai.model.User;
import com.bjpowernode.licai.service.UserService;
import com.bjpowernode.web.config.RealnameConfig;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class RealNameService {

    @DubboReference(interfaceClass = UserService.class,version = "1.0")
    private UserService userService;

    @Resource
    private RealnameConfig realnameConfig;

    public boolean realName(String phone,String name,String card){
        boolean result = false;
        // 查询phone 对应的用户是否存在
        User user  = userService.queryByPhone(phone);
        if( user != null){

            //调用第三方的接口，使用HttpClient
            Map<String,Object > map  = new HashMap<>();
            map.put("cardNo",card);
            map.put("realName",name);
            map.put("appkey",realnameConfig.getAppkey());

            try {
                //String json  = HttpClientUtils.doPost(realnameConfig.getUrl(),map);
                String json = "{\"code\":\"10000\",\"charge\":false,\"remain\":0,\"msg\":\"查询成功\",\"result\":{\"error_code\":206501,\"reason\":\"库中无此身份证记录\",\"result\":{\"realname\":\"睡**\",\"idcard\":\"110101************\",\"isok\":true,\"IdCardInfor\":{\"province\":\"北京市\",\"city\":\"北京市\",\"district\":\"东城区\",\"area\":\"北京市东城区\",\"sex\":\"男\",\"birthday\":\"1990-3-7\"}}},\"requestId\":\"3f81ea3add0f4cde84c1dad617bc91d2\"}";

                //处理返回的json
                if(json != null){
                    JSONObject obj  = JSONObject.parseObject(json);
                    if("10000".equals(obj.getString("code"))){
                        result = obj.getJSONObject("result")
                                    .getJSONObject("result").getBooleanValue("isok");
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
