package com.bjpowernode.web.config;

import com.bjpowernode.web.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcWebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LoginInterceptor loginInterceptor = new LoginInterceptor();

        //拦截 所有以 loan 开头的请求
        String addPath [] =  { "/loan/**"};

        //排除不需要拦截的
        String excludePath [] = {
                "/loan/phone/register",
                "/loan/sendCode",
                "/loan/loan",
                "/loan/loanInfo",
                "/loan/login",
                "/loan/logout",
                "/loan/page/login",
                "/loan/page/register",
                "/loan/register"

        };
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns(addPath)
                .excludePathPatterns(excludePath);

    }
}
