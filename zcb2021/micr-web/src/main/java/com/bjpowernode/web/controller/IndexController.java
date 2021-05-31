package com.bjpowernode.web.controller;

import com.bjpowernode.contants.CommonContants;
import com.bjpowernode.licai.model.LoanInfo;
import com.bjpowernode.licai.service.BidInfoService;
import com.bjpowernode.licai.service.LoanInfoService;
import com.bjpowernode.licai.service.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class IndexController {

    @DubboReference(interfaceClass = UserService.class,version = "1.0")
    private UserService userService;


    @DubboReference(interfaceClass = BidInfoService.class,version = "1.0")
    private BidInfoService bidInfoService;


    @DubboReference(interfaceClass = LoanInfoService.class,version = "1.0")
    private LoanInfoService loanInfoService;


    @GetMapping({"/index","/"})
    public String index(Model model){

        //获取平台注册总用户数量
        int countUsers = userService.queryRegisterUserCount();
        model.addAttribute("registerUsers",countUsers);

        //获取累计投资金额
        BigDecimal sumBidMoney = bidInfoService.querySumBidMoney();
        model.addAttribute("sumBidMoney",sumBidMoney);

        //获取平均收益率
        BigDecimal avgHistoryRate = loanInfoService.queryHistoryAvgRate();
        model.addAttribute("avgHistoryRate",avgHistoryRate);

        //获取的新手宝的产品
        List<LoanInfo> loanInfoList = loanInfoService.queryPageByType(
                                 CommonContants.LAON_PRODUCT_TYPE_XINSHOUBAO_0,1,1);
        model.addAttribute("xinShouBaoList",loanInfoList);

        //优选的产品
        loanInfoList = loanInfoService.queryPageByType(
                                 CommonContants.LAON_PRODUCT_TYPE_YOUXUAN_1,1,4 );
        model.addAttribute("youXuanList",loanInfoList);


        //获取散标的产品
        loanInfoList = loanInfoService.queryPageByType(
                                 CommonContants.LAON_PRODUCT_TYPE_SANBIAO_2,1,8 );
        model.addAttribute("sanBiaoList",loanInfoList);

        return "index";
    }
}
