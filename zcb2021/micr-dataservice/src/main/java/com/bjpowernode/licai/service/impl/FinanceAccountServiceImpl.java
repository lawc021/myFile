package com.bjpowernode.licai.service.impl;

import com.bjpowernode.licai.mapper.FinanceAccountMapper;
import com.bjpowernode.licai.model.FinanceAccount;
import com.bjpowernode.licai.service.FinanceAccountService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService(interfaceClass = FinanceAccountService.class,version = "1.0")
public class FinanceAccountServiceImpl implements FinanceAccountService {

    @Resource
    private  FinanceAccountMapper financeAccountMapper;

    @Override
    public FinanceAccount queryAccount(Integer uid) {
        FinanceAccount financeAccount = financeAccountMapper.selectByUid(uid);
        return financeAccount;
    }
}
