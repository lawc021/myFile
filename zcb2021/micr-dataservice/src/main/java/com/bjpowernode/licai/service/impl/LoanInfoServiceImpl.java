package com.bjpowernode.licai.service.impl;

import com.bjpowernode.commmon.CommonUtil;
import com.bjpowernode.commmon.PageUtil;
import com.bjpowernode.licai.mapper.LoanInfoMapper;
import com.bjpowernode.licai.model.LoanInfo;
import com.bjpowernode.licai.model.mix.LoanBidInfo;
import com.bjpowernode.licai.service.LoanInfoService;
import org.apache.dubbo.common.utils.Page;
import org.apache.dubbo.config.annotation.DubboService;
import sun.security.x509.AVA;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@DubboService(interfaceClass = LoanInfoService.class,version = "1.0")
public class LoanInfoServiceImpl implements LoanInfoService {

    @Resource
    private LoanInfoMapper loanInfoMapper;

    @Override
    public BigDecimal queryHistoryAvgRate() {
        BigDecimal avgHistoryRate = loanInfoMapper.selectAvgRate();
        return avgHistoryRate;
    }

    /**
     * 按类型分页查询产品
     * @param productType 产品类型
     * @param pageNo      第几页
     * @param pageSize    每页大小
     * @return
     */
    @Override
    public List<LoanInfo> queryPageByType(Integer productType,
                                          Integer pageNo,
                                          Integer pageSize) {

        List<LoanInfo> loanInfoList =  new ArrayList<>();
        if(CommonUtil.checkProductType(productType)){
            //数据检查
            pageNo= PageUtil.defaultPageNo(pageNo);
            pageSize = PageUtil.defaultPageSize(pageSize);

            //计算offSet
            int offSet = (pageNo -1 ) * pageSize;
            loanInfoList = loanInfoMapper.selectPageByType(productType,offSet, pageSize);
        }
        return loanInfoList;
    }
    /**
     * 获取分页查询产品总记录数
     */
    @Override
    public int queryRecordNumsPageByType(Integer productType) {
        int nums = 0;
        if(CommonUtil.checkProductType(productType)){
            nums = loanInfoMapper.selectCountRecordByType(productType);
        }
        return nums;
    }

    /**
     * 使用主键，查询产品的信息
     */
    @Override
    public LoanInfo queryByLoanId(Integer loanId) {

        LoanInfo loanInfo = null;
        if( loanId != null && loanId.intValue() > 0 ){
            loanInfo = loanInfoMapper.selectByPrimaryKey(loanId);
        }
        return loanInfo;
    }

    /**
     * 某产品的最近3条投资记录
     */
    @Override
    public List<LoanBidInfo> queryBidInfoByLoanId(Integer loanId) {
        List<LoanBidInfo> loanBidInfoList
                = loanInfoMapper.selectBidInfoByLoanId(loanId);
        return loanBidInfoList;
    }
}
