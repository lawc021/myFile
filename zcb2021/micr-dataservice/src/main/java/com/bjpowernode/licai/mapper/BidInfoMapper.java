package com.bjpowernode.licai.mapper;

import com.bjpowernode.licai.model.BidInfo;
import com.bjpowernode.licai.model.mix.UserBidInfo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BidInfoMapper {

    //累计成交投资金额
    BigDecimal selectSumBidMoney();


    //某个用户的最近投资记录
    List<UserBidInfo> selectPageUserBidInfo(@Param("uid") Integer uid,
                                            @Param("offset") Integer offset,
                                            @Param("rows") Integer rows);


    //产品产品的投资记录
    List<BidInfo> selectByLoanId(@Param("loanId") Integer loanId);

    int deleteByPrimaryKey(Integer id);

    int insert(BidInfo record);

    int insertSelective(BidInfo record);

    BidInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BidInfo record);

    int updateByPrimaryKey(BidInfo record);
}