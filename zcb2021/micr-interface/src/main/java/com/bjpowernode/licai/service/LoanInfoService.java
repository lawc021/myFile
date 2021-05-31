package com.bjpowernode.licai.service;

import com.bjpowernode.licai.model.LoanInfo;
import com.bjpowernode.licai.model.mix.LoanBidInfo;

import java.math.BigDecimal;
import java.util.List;

public interface LoanInfoService {

    //收益率的平均值
    BigDecimal queryHistoryAvgRate();

    /**
     * 分页查询产品
     * @param productType 产品类型
     * @param pageNo      第几页
     * @param pageSize    每页大小
     * @return
     */
    List<LoanInfo> queryPageByType(Integer productType,
                                   Integer pageNo,
                                   Integer pageSize);

    /**
     * 获取分页查询产品总记录数
     */
    int queryRecordNumsPageByType(Integer productType);

    /**
     * 使用主键，查询产品的信息
     */
    LoanInfo queryByLoanId(Integer loanId);

    /**
     * 某产品的最近3条投资记录
     */
    List<LoanBidInfo> queryBidInfoByLoanId(Integer loanId);
}
