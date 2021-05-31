package com.bjpowernode.licai.service.impl;

import com.bjpowernode.commmon.CommonUtil;
import com.bjpowernode.contants.RedisKey;
import com.bjpowernode.licai.mapper.FinanceAccountMapper;
import com.bjpowernode.licai.mapper.UserMapper;
import com.bjpowernode.licai.model.FinanceAccount;
import com.bjpowernode.licai.model.User;
import com.bjpowernode.licai.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@DubboService(interfaceClass = UserService.class,version = "1.0")
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private FinanceAccountMapper financeAccountMapper;

    @Resource
    private RedisTemplate redisTemplate;

    //创建一个日志门面（slf4j）中的对象
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * @return 平台注册用户数量
     */
    @Override
    public int queryRegisterUserCount() {

        //使用logger对象的方法，输出信息
        logger.debug("queryRegisterUserCount 调试信息：开始执行方法"+ new Date());

        //1.从redis获取数据
        ValueOperations vo = redisTemplate.opsForValue();
        Integer countUsers = (Integer) vo.get(RedisKey.APP_REGISTER_USER);

        logger.debug("queryRegisterUserCount 调试信息,从redis获取的信息："+countUsers);
        if( countUsers == null ){ //从redis没有获取数据
            synchronized (this){
                //再查redis的数据
                countUsers = (Integer) vo.get(RedisKey.APP_REGISTER_USER);
                if( countUsers == null ){
                    //2.从数据库获取数据
                    countUsers = userMapper.selectCountUser();
                    //3.把数据放入到redis
                    vo.set(RedisKey.APP_REGISTER_USER,countUsers,30, TimeUnit.MINUTES);
                }
            }
        }
        logger.info("queryRegisterUserCount 信息："+countUsers);
        logger.debug("queryRegisterUserCount 调试信息，方法执行完成："+ new Date());
        return countUsers;
    }

    @Override
    public User queryByPhone(String phone) {

        User user  = null;
        if (CommonUtil.checkPhone(phone)) {
            user = userMapper.selectByPhone(phone);
        }
        return user;
    }

    /**
     * 用户注册
     * @param phone
     * @param pwd
     * @return
     */
    @Transactional
    @Override
    public User userRegister(String phone, String pwd) {
        //1.判断手机号是否注册过
        User user = userMapper.selectByPhone(phone);
        if( user == null){
            //2. 添加u_user记录，返回id
            user = new User();
            user.setPhone(phone);
            user.setLoginPassword(pwd);
            user.setAddTime(new Date());
            int rows  = userMapper.insertUserReturnId(user);
            if( rows < 1){
                throw new RuntimeException("添加用户异常");
            }
            //3.添加u_finace_account
            FinanceAccount account = new FinanceAccount();
            account.setUid( user.getId());
            account.setAvailableMoney(new BigDecimal("888"));

            rows = financeAccountMapper.insertSelective(account);
            if( rows < 1){
                throw new RuntimeException("添加资金账户异常");
            }
        } else {
            user = null;
        }
        return user;
    }

    /**
     * 更新用户
     * @param user
     * @return
     */
    @Override
    public int modifyUser(User user) {
        int rows  = userMapper.updateByPrimaryKeySelective(user);
        return rows;
    }

    /**
     * 登录
     * @param phone    手机号
     * @param password 密码
     * @return
     */
    @Override
    public User login(String phone, String password) {

        User user  = userMapper.selectByPhone(phone);
        if(user != null){
            if( user.getLoginPassword().equals(password)){
                //登录成功，更新此用户登录时间
                user.setLastLoginTime( new Date());
                userMapper.updateByPrimaryKeySelective(user);
            } else {
                user = null;
            }
        }
        return user;
    }








   /* @Override
    public User userRegister(String phone, String pwd) {

        //1.判断手机号是否注册过
        User user = userMapper.selectByPhone(phone);
        if( user == null){
            user.setPhone(phone);
            user.setLoginPassword(pwd);
            userMapper.insertSelective(user);

            //查询phone的对应的userd id
            user = userMapper.selectByPhone(phone);
            Integer id = user.getId();

            //添加u_finace_account

        }
        return null;
    }*/
}
