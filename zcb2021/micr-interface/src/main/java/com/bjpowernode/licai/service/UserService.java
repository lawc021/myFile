package com.bjpowernode.licai.service;

import com.bjpowernode.licai.model.User;

public interface UserService {


    /**
     * @return 平台注册用户数量
     */
    int queryRegisterUserCount();

    /**
     * 按手机号查询
     * @param phone
     * @return
     */
    User queryByPhone(String phone);


    /**
     * 用户注册
     * @param phone
     * @param pwd
     * @return
     */
    User userRegister(String phone, String pwd);


    /**
     * 更新用户
     */
    int modifyUser(User user);


    /**
     * 登录
     * @param phone    手机号
     * @param password 密码
     * @return
     */
    User login(String phone, String password);
}
