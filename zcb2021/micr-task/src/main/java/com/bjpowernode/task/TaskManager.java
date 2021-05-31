package com.bjpowernode.task;

import com.bjpowernode.commmon.HttpClientUtils;
import com.bjpowernode.licai.service.IncomeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sun.net.www.http.HttpClient;

import java.util.Date;

@Component("taskManager")
public class TaskManager {

    @Value("${micrpay.alipay.url}")
    private String micrPayAlipayQueryUrl;

    @DubboReference(interfaceClass = IncomeService.class,version = "1.0")
    private IncomeService incomeService;

    /**
     * 创建方法，表示定时任务的功能
     * 1.方法是public
     * 2.方法没有参数
     * 3.方法没有返回值
     *
     * @Scheduled: 指定定时任务
     *   位置：放在方法的上面， 方法定时执行
     *   属性： cron 时间表达式
     */
    //@Scheduled(cron = "*/10 * * * * ?")
    public void testCron(){
        System.out.println("执行定时任务："+ new Date());
    }



    //调用生成收益计划的方法
    @Scheduled(cron = "0 */20 * * * ?")
    public void genernateIncomePlan(){
        //调用dubbo服务提供者
        incomeService.generateIncomePlan();
    }

    //调用生成收益返还
    @Scheduled(cron = "0 */30 * * * ?")
    public void generateIncomeBack(){
        incomeService.generateIncomeBack();
    }

    //定时任务：调用micr-pay的支付，调用支付宝的查询接口
    @Scheduled(cron = "0 */10 * * * ?")
    public void invokeMicrPayAlipayQuery(){
        //使用HttpClient调用 controller接口
        try {
            String result =  HttpClientUtils.doGet(micrPayAlipayQueryUrl);
            System.out.println("调用支付微服务返回的结果："+result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
