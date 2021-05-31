package com.bjpowernode.web.controller;

import com.bjpowernode.commmon.HttpClientUtils;
import com.bjpowernode.contants.CommonContants;
import com.bjpowernode.licai.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sound.midi.Soundbank;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class RechargeController {

    @Value("${pay.alipay.url}")
    private String micrPayAliapyUrl;
    //显示支付页面
    @GetMapping("/loan/page/toRecharge")
    public String pageRecharge(){
        return "toRecharge";
    }

    //支付宝入口
    @PostMapping("/loan/toRecharge/alipay")
    public void alipayEntry(@RequestParam("rechargeMoney") BigDecimal money,
                              HttpSession session,
                              HttpServletResponse response){

        System.out.println("alipayEntry");

        User user  = (User) session.getAttribute(CommonContants.SESSION_USER_KEY);
        //向micr-pay发起请求。
        String url = micrPayAliapyUrl;
        Map<String,Object> params= new HashMap<>();
        params.put("uid",user.getId());
        params.put("money",money);
        params.put("channel","alipay");
        try {
            //result 是一个form
            String result =  HttpClientUtils.doPost(url,params);
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.println( "<html>"+result+"</html>");
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
