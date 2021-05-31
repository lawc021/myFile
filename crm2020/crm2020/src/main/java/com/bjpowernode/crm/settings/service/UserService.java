package com.bjpowernode.crm.settings.service;

import com.bjpowernode.crm.settings.domain.TblUser;
import com.bjpowernode.crm.settings.domain.User;

import java.util.List;
import java.util.Map;

public interface UserService {
	/**
	 * 按id获取use对象
	 * 
	 * @return
	 */
	public TblUser  selectUserById(String id);

	User queryUserByLoginActAndPwd(Map<String,Object> map);

	List<User> queryAllUsers();
}
