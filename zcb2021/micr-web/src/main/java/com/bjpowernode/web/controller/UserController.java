package com.bjpowernode.web.controller;

import com.bjpowernode.commmon.CommonUtil;
import com.bjpowernode.contants.CommonContants;
import com.bjpowernode.licai.model.FinanceAccount;
import com.bjpowernode.licai.model.User;
import com.bjpowernode.licai.model.mix.UserBidInfo;
import com.bjpowernode.licai.model.mix.UserIncomeInfo;
import com.bjpowernode.licai.model.mix.UserRechargeInfo;
import com.bjpowernode.licai.service.*;
import com.bjpowernode.vo.ResultObject;
import com.bjpowernode.web.service.RealNameService;
import com.bjpowernode.web.service.SmsService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class UserController {

    @DubboReference(interfaceClass = UserService.class,version = "1.0")
    private UserService userService;

    @DubboReference(interfaceClass = BidInfoService.class,version = "1.0")
    private BidInfoService bidInfoService;

    @DubboReference(interfaceClass = FinanceAccountService.class,version = "1.0")
    private FinanceAccountService financeAccountService;

    @DubboReference(interfaceClass = RechargeService.class,version = "1.0")
    private RechargeService rechargeService;

    @DubboReference(interfaceClass = IncomeService.class,version = "1.0")
    private IncomeService incomeService;

    @Resource
    private SmsService smsService;

    @Resource
    private RealNameService realNameService;

    //进入注册页面
    @GetMapping("/loan/page/register")
    public String pageRegister(Model model){
        return "register";
    }

    //用户中心
    @GetMapping("/loan/myCenter")
    public String pageMyCenter(HttpSession session,Model model) {
        BigDecimal accountMoney = new BigDecimal("0");
        //用户金额
        User user  = (User) session.getAttribute( CommonContants.SESSION_USER_KEY);
        FinanceAccount account  = financeAccountService.queryAccount(user.getId());
        if(account != null){
            accountMoney = account.getAvailableMoney();
        }

        //最近投资记录
        List<UserBidInfo> userBidInfoList = bidInfoService.queryRecentlyUserBidInfo(user.getId(),0,5);

        //最近的充值记录
        List<UserRechargeInfo> userRechargeInfoList =
                rechargeService.queryRecentlyUserRechargeInfo(user.getId(),0,5);


        //最近的收益记录
        List<UserIncomeInfo> userIncomeInfoList =incomeService.queryRecentlyUserIncome(user.getId(),0,5);

        model.addAttribute("accountMoney",accountMoney);
        model.addAttribute("userBidInfoList",userBidInfoList);
        model.addAttribute("userRechargeList",userRechargeInfoList);
        model.addAttribute("userIncomeList",userIncomeInfoList);
        return "myCenter";
    }


    //实名认证的页面
    @GetMapping("/loan/realName")
    public String pageRealName(Model model,HttpSession session){

        User user  = (User) session.getAttribute(CommonContants.SESSION_USER_KEY);
        model.addAttribute("phone",user.getPhone());
        return "realName";
    }

    //登录的页面
    @GetMapping("/loan/page/login")
    public String pageLogin(@RequestParam(value = "returnUrl",required = false) String returnUrl,
                            Model  model) {

        model.addAttribute("returnUrl",returnUrl);

        int regiserUser  = userService.queryRegisterUserCount();
        model.addAttribute("registerUsers",regiserUser);

        //获取累计投资金额
        BigDecimal sumBidMoney = bidInfoService.querySumBidMoney();
        model.addAttribute("sumBidMoney",sumBidMoney);


        return "login";
    }


    //注册用户
    @PostMapping("/loan/register")
    @ResponseBody
    public ResultObject userRegister(@RequestParam("phone") String phone,
                                     @RequestParam("pwd") String password,
                                     @RequestParam("code") String code,
                                     HttpSession session){
        ResultObject ro  = ResultObject.error("注册失败");

        if(CommonUtil.checkPhone(phone) && code != null  ){
            // 判断code是否有效
            if(smsService.matchCode(phone,code)){
                User user = userService.userRegister(phone,password);
                //注册是否成功
                if( user != null){
                    //注册成功
                    ro = ResultObject.success("注册成功");
                    //放到session
                    session.setAttribute(CommonContants.SESSION_USER_KEY,user);
                }
            } else {
                ro.setMessage("验证码无效");
            }
        }  else {
            ro.setMessage("手机号或者验证码无效");
        }

        return ro;
    }


    //实名认证
    @PostMapping("/loan/realName")
    @ResponseBody
    public ResultObject realName(@RequestParam("name") String name,
                                 @RequestParam("card") String card,
                                 @RequestParam("phone") String phone,
                                 HttpSession session){
        ResultObject ro = ResultObject.error("认证失败,稍候重试");
        if( name == null || name.length()< 2){
            ro.setMessage("姓名不正确");
        } else if( card == null || ( card.length() < 15 || card.length()> 18 ) ){
            ro.setMessage("身份证号不正确");
        } else if( !CommonUtil.checkPhone(phone)){
            ro.setMessage("手机号不正确");
        } else {
            //进行实名认证处理
            boolean result  = realNameService.realName(phone,name,card);
            if(result){
                //更新用户的数据
                User user = (User) session.getAttribute(CommonContants.SESSION_USER_KEY);
                user.setIdCard(card);
                user.setName(name);
                int rows = userService.modifyUser(user);
                //创建success的 ro 对象
                if( rows > 0){
                    ro  = ResultObject.success("实名认证成功");
                }
            }
        }

        return ro;
    }


    //登录
    @PostMapping("/loan/login")
    @ResponseBody
    public ResultObject login(@RequestParam("phone") String phone,
                              @RequestParam("pwd") String password,
                              HttpSession session){
        ResultObject ro  = ResultObject.error("登录失败");
        if( CommonUtil.checkPhone(phone)
                && password != null && password.length() == 32 ){

            //调用登录的业务方法
            User user  = userService.login(phone,password);
            //登录成功，user放入到session
            if( user != null){
                ro  = ResultObject.success("登录成功");
                session.setAttribute(CommonContants.SESSION_USER_KEY,user);
            } else {
                ro.setMessage("登录手机号或者密码无效");
            }

        } else {
            ro.setMessage("登录手机号或者密码无效");
        }
        return ro;
    }

    //退出
    @GetMapping("/loan/logout")
    public String logout(HttpSession session){
        // session无效
        session.removeAttribute(CommonContants.SESSION_USER_KEY);
        session.invalidate();

        //重新回到首页
        return "redirect:/index";
    }
}


