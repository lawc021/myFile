package com.bjpowernode.licai.mapper;

import com.bjpowernode.licai.model.FinanceAccount;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface FinanceAccountMapper {

    FinanceAccount selectByUid(@Param("uid") Integer uid);

    //查询的数据上锁
    FinanceAccount selectUidForUpdate(@Param("uid") Integer uid);

    //投资，扣除金额
    int updateAvaiableMoneyInvest(@Param("uid") Integer uid, @Param("bidMoney") BigDecimal bidMoney);


    //收益，增加金额
    int updateAvaiableMoneyIncome(@Param("uid") Integer uid,
                                  @Param("bidMoney") BigDecimal bidMoney,
                                  @Param("incomeMoney") BigDecimal incomeMoney);

    //充值，增加金额
    int updateAvaiableMoneyRecharge(@Param("uid") Integer uid,
                                    @Param("rechargeMoney") BigDecimal rechargeMoney);

    int deleteByPrimaryKey(Integer id);

    int insert(FinanceAccount record);

    int insertSelective(FinanceAccount record);

    FinanceAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FinanceAccount record);

    int updateByPrimaryKey(FinanceAccount record);
}