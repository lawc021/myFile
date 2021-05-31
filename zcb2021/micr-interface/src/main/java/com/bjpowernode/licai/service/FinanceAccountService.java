package com.bjpowernode.licai.service;

import com.bjpowernode.licai.model.FinanceAccount;

public interface FinanceAccountService {

    //根据uid查询账号金额
    FinanceAccount queryAccount(Integer uid);
}
