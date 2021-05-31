package com.bjpowernode.licai.service;

import com.bjpowernode.licai.model.mix.UserIncomeInfo;

import java.util.List;

//收益的service
public interface IncomeService {

    //某个用户最近的收益记录
    List<UserIncomeInfo> queryRecentlyUserIncome(Integer uid,
                                                 Integer pageNo,
                                                 Integer pageSize);

    //生成收益计划
    boolean generateIncomePlan();

    //收益返还
    boolean generateIncomeBack();

}
