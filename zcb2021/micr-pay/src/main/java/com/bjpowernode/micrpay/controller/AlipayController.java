package com.bjpowernode.micrpay.controller;

import com.bjpowernode.micrpay.service.AlipayService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
public class AlipayController {

    @Resource
    private AlipayService alipayService;

    //接收 micr-web 服务发送过来的下单的请求
    @PostMapping("/alipay/entry")
    @ResponseBody
    public String alipyEntry(@RequestParam("uid") Integer uid,
                             @RequestParam("money") BigDecimal money,
                             @RequestParam("channel") String channel){
        System.out.println("uid="+uid+", money="+money+", channel="+channel);
        String form  = alipayService.alipayPagePay(uid,money,channel);
        return form;
    }

    //接收支付宝发送的post请求。 接收充值结果
    @PostMapping("/alipay/notify")
    public void alipayNotify(HttpServletRequest request,
                             HttpServletResponse response){
        //接收支付宝传递过来的所有请求参数
        try{
            Map<String,String> params = new HashMap<String,String>();
            Map<String,String[]> requestParams = request.getParameterMap();
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
                // params 是参数的集合
                params.put(name, valueStr);
            }
            //调用service，处理notify其他步骤
            alipayService.alipyNotify(params);

        } catch (Exception e){
            e.printStackTrace();
            //记录错误的订单号，到日志文件，或者发送邮件，把没有处理成功的订单号给客服人员。
        } finally {
            //输出success给支付宝
            try {
                PrintWriter out = response.getWriter();
                out.println("success");
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //调用支付宝的查询接口。定时任务
    @GetMapping("/alipay/query")
    @ResponseBody
    public String alipayQuery(){
        alipayService.alipyQuery();
        return "调用接口成功";
    }
}
