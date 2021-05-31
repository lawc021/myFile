package com.bjpowernode.licai.service;

import com.bjpowernode.licai.model.mix.UserBidInfo;

import java.math.BigDecimal;
import java.util.List;

public interface BidInfoService {

    /**
     * @return 累计投资金额
     */
    BigDecimal querySumBidMoney();


    /**
     * 用户最近的投资记录，分页
     */
    List<UserBidInfo> queryRecentlyUserBidInfo(Integer uid,
                                               Integer pageNo,
                                               Integer pageSize);

    /**
     * 投资
     * @param uid  用户id
     * @param loanId 产品id
     * @param bidMoney 投资金额
     * @return
     */
    boolean invest(Integer uid, Integer loanId, BigDecimal bidMoney);
}
