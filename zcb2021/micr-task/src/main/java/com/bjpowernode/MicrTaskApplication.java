package com.bjpowernode;

import com.bjpowernode.task.TaskManager;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

//启用Dubbo服务
@EnableDubbo
//启动定时任务
@EnableScheduling
@SpringBootApplication
public class MicrTaskApplication {

	public static void main(String[] args) {

		ApplicationContext ctx = SpringApplication.run(MicrTaskApplication.class, args);

		TaskManager tm = (TaskManager) ctx.getBean("taskManager");

		//tm.genernateIncomePlan();
		//tm.generateIncomeBack();

		tm.invokeMicrPayAlipayQuery();

	}

}
