package com.bjpowernode.web.controller;

import com.bjpowernode.commmon.CommonUtil;
import com.bjpowernode.contants.CommonContants;
import com.bjpowernode.contants.RedisKey;
import com.bjpowernode.licai.model.FinanceAccount;
import com.bjpowernode.licai.model.User;
import com.bjpowernode.licai.service.FinanceAccountService;
import com.bjpowernode.licai.service.UserService;
import com.bjpowernode.vo.ResultObject;
import com.bjpowernode.web.service.SmsService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@RestController
public class HelpController {


    @DubboReference(interfaceClass = UserService.class,version = "1.0")
    private UserService userService;

    @DubboReference(interfaceClass = FinanceAccountService.class,version = "1.0")
    private FinanceAccountService financeAccountService;

    @Resource
    private SmsService smsService;

    @Resource
    private RedisTemplate redisTemplate;
    /**
     * 判断手机号是否注册过
     */
    @GetMapping("/loan/phone/register")
    public ResultObject phoneRegister(@RequestParam("phone") String phone){
        ResultObject ro  = ResultObject.error("请更换手机号");
        //判断phone的格式
        if(CommonUtil.checkPhone(phone)){
            //手机号是正确的，查询数据库
            User user  = userService.queryByPhone(phone);
            if( user == null ){
                // 可以注册
                ro = ResultObject.success("可以注册");
            }
        }
        return ro;
    }


    //发送验证码
    @GetMapping("/loan/sendCode")
    public ResultObject sendCode(@RequestParam("phone") String phone){
        ResultObject ro = ResultObject.error("发送失败");
        if( CommonUtil.checkPhone(phone)){
            //可以发送验证码
            StringBuilder builder = new StringBuilder("");
            boolean result  = smsService.sendSmsCode(phone,builder);
            if( result ){
                // 存放到redis，设置3分钟有效
                String k = RedisKey.APP_REGISTER_SMS_CODE + phone;
                redisTemplate.opsForValue().set(k,builder.toString(),3, TimeUnit.MINUTES);
                ro = ResultObject.success("短信下发成功");
            }
        } else {
            ro.setMessage("手机号格式不正确");
        }
        return ro;
    }


    //查询金额
    @GetMapping("/loan/account")
    public ResultObject queryAccount(HttpSession session){
        ResultObject ro  = ResultObject.error("获取失败");
        BigDecimal accountMoney = new BigDecimal("0");
        //从session中获取user
        User user = (User) session.getAttribute(CommonContants.SESSION_USER_KEY);
        if( user != null ){
            FinanceAccount account = financeAccountService.queryAccount(user.getId());
            if( account != null ){
                accountMoney = account.getAvailableMoney();
                ro = ResultObject.success("查询成功");
                ro.setData(accountMoney);
            }
        }
        return ro;
    }
}
