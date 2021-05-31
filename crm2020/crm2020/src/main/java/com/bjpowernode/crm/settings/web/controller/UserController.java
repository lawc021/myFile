package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.MD5Util;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    /*
        /代表应用的根目录,即http://127.0.0.1:8080/crm
        从前台访问：http://127.0.0.1:8080/crm/settings/qx/user/toLogin.do
     */
    @RequestMapping("/settings/qx/user/toLogin.do")
    public String toLogin(HttpServletRequest request) {
        /*
            在跳转页面时，前缀+"settings/qx/user/login"+后缀，查找目标页面。
         */
        //跳转到登录页面
        //return "settings/qx/user/login";//   /WEB-INF/pages/settings/qx/user/login.jsp
        Cookie[] cookies = request.getCookies();

        String loginAct = null;
        String loginPwd = null;
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            if ("loginAct".equals(name)) {
                loginAct = cookie.getValue();
                continue;
            }
            if ("loginPwd".equals(name)) {
                loginPwd = cookie.getValue();
            }
        }
        if (loginAct != null && loginPwd != null) {
            //封装参数
            Map<String, Object> map = new HashMap<>();
            map.put("loginAct", loginAct);
            map.put("loginPwd", MD5Util.getMD5(loginPwd));
            User user = userService.queryUserByLoginActAndPwd(map);
            //注意拦截器中是判断在session中是否有sessionUser中存放user对象
            request.getSession().setAttribute("sessionUser", user);
            return "redirect:/workbench/index.do";
        } else {
            return "settings/qx/user/login";
        }
    }

    @RequestMapping("/settings/qx/user/login.do")
    public @ResponseBody
    Object login(String loginAct, String loginPwd, String isRemPwd, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        //封装参数
        Map<String, Object> map = new HashMap<>();
        map.put("loginAct", loginAct);
        map.put("loginPwd", MD5Util.getMD5(loginPwd));
        //调用service层方法，查询用户
        User user = userService.queryUserByLoginActAndPwd(map);

        //根据查询结果，生成响应信息
        ReturnObject returnObject = new ReturnObject();
        if (user == null) {
            //用户名或者密码错误，登录失败
            //{code=0,message="用户名或者密码错误"}
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("用户名或者密码错误");
        } else {
            if (DateUtils.formatDateTime(new Date()).compareTo(user.getExpireTime()) > 0) {
                //账号已经过期，登录失败
                //{code=0,message="账号已经过期"}
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("账号已经过期");
            } else if ("0".equals(user.getLockState())) {
                //状态被锁定，登录失败
                //{code=0,message="状态被锁定"}
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("状态被锁定");
            } else if (!user.getAllowIps().contains(request.getRemoteAddr())) {
                //ip受限，登录失败
                //{code=0,message="ip受限"}
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("ip受限");
            } else {
                //登录成功
                //{code=1}
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);

                //把用户的信息保存到session中
                session.setAttribute(Contants.SESSION_USER, user);

                //如果需要记住密码，则把loginAct和loginPwd以cookie的形式返回客户端
                if ("true".equals(isRemPwd)) {
                    Cookie c1 = new Cookie("loginAct", loginAct);
                    c1.setMaxAge(10 * 24 * 60 * 60);
                    response.addCookie(c1);

                    Cookie c2 = new Cookie("loginPwd", loginPwd);
                    c2.setMaxAge(10 * 24 * 60 * 60);
                    response.addCookie(c2);
                } else {//如果不需要记住密码，则把loginAct和loginPwd的cookie删除
                    Cookie c1 = new Cookie("loginAct", null);
                    c1.setMaxAge(0);
                    response.addCookie(c1);

                    Cookie c2 = new Cookie("loginPwd", null);
                    c2.setMaxAge(0);
                    response.addCookie(c2);
                }
            }
        }

        return returnObject;
    }

    @RequestMapping("/settings/qx/user/logout.do")
    public String logout(HttpServletResponse response, HttpSession session) {
        //清空cookie
        Cookie c1 = new Cookie("loginAct", null);
        c1.setMaxAge(0);
        response.addCookie(c1);

        Cookie c2 = new Cookie("loginPwd", null);
        c2.setMaxAge(0);
        response.addCookie(c2);

        //销毁session
        session.invalidate();

        //重定向
        return "redirect:/";//springmvc会转换成：response.sendRedirect("/crm");，然后执行。
    }
}
