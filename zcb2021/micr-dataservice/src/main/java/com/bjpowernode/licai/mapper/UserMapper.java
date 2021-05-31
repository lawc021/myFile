package com.bjpowernode.licai.mapper;

import com.bjpowernode.licai.model.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    //查询注册用户总数
    int selectCountUser();


    //使用手机号，查询用户
    User selectByPhone(@Param("phone") String phone);

    //添加user，返回id
    int insertUserReturnId(User user);

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);


}