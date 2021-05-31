package com.bjpowernode.licai.service.impl;

import com.bjpowernode.commmon.CommonUtil;
import com.bjpowernode.commmon.DecimalUtil;
import com.bjpowernode.commmon.PageUtil;
import com.bjpowernode.contants.CommonContants;
import com.bjpowernode.licai.mapper.BidInfoMapper;
import com.bjpowernode.licai.mapper.FinanceAccountMapper;
import com.bjpowernode.licai.mapper.LoanInfoMapper;
import com.bjpowernode.licai.model.BidInfo;
import com.bjpowernode.licai.model.FinanceAccount;
import com.bjpowernode.licai.model.LoanInfo;
import com.bjpowernode.licai.model.mix.UserBidInfo;
import com.bjpowernode.licai.service.BidInfoService;
import com.bjpowernode.licai.service.LoanInfoService;
import javafx.scene.control.ComboBox;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@DubboService(interfaceClass = BidInfoService.class,version = "1.0")
public class BidInfoServiceImpl implements BidInfoService {

    @Resource
    private BidInfoMapper bidInfoMapper;

    @Resource
    private FinanceAccountMapper financeAccountMapper;

    @Resource
    private LoanInfoMapper loanInfoMapper;

    @Override
    public BigDecimal querySumBidMoney() {
        log.info("使用lombok的log日志对象");
        BigDecimal sumBidMoney = bidInfoMapper.selectSumBidMoney();
        return sumBidMoney;
    }

    @Override
    public List<UserBidInfo> queryRecentlyUserBidInfo(Integer uid,
                                                      Integer pageNo,
                                                      Integer pageSize) {

        List<UserBidInfo> list = new ArrayList<>();
        if( uid != null && uid > 0){
            pageNo = PageUtil.defaultPageNo(pageNo);
            pageSize = PageUtil.defaultPageSize(pageSize);

            int offset = (pageNo  - 1) * pageSize;
            list = bidInfoMapper.selectPageUserBidInfo(uid,offset,pageSize);

        }
        return list;
    }

    /**
     * 投资
     * @param uid  用户id
     * @param loanId 产品id
     * @param bidMoney 投资金额
     * @return true：投资成功， false 投资失败
     */
    @Transactional
    @Override
    public boolean invest(Integer uid, Integer loanId, BigDecimal bidMoney) {
        int rows = 0;
        boolean  result  = false;
        //1.检查用户的金额
        FinanceAccount account = financeAccountMapper.selectUidForUpdate(uid);
        if( account != null){
            if(DecimalUtil.le( bidMoney, account.getAvailableMoney())){
                //资金充足， 扣除金额
                rows = financeAccountMapper.updateAvaiableMoneyInvest(uid,bidMoney);
                if( rows < 1){
                    throw new RuntimeException("投资-扣除用户资金失败");
                }

                //处理产品
                LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey(loanId);
                if( loanInfo != null){
                    //状态，必须 0 的状态才能购买
                    if( loanInfo.getProductStatus() == CommonContants.LOAN_STATUS_WEIMANBIAO){
                        // bidMoney >= min <= max   , <leftMoney
                        if( DecimalUtil.le(bidMoney,loanInfo.getLeftProductMoney())
                            && DecimalUtil.ge(bidMoney,loanInfo.getBidMinLimit())
                            && DecimalUtil.le(bidMoney,loanInfo.getBidMaxLimit()) ){
                            //可以投资
                            //扣除产品的 leftMoney
                            rows  = loanInfoMapper.updateLeftMoneyInvest(loanId,bidMoney);
                            if( rows < 1){
                                throw new RuntimeException("投资-扣除产品的剩余可投资金额失败");
                            }

                            //创建投资记录
                            BidInfo bidInfo = new BidInfo();
                            bidInfo.setBidMoney(bidMoney);
                            bidInfo.setBidStatus(1);
                            bidInfo.setBidTime(new Date());
                            bidInfo.setLoanId(loanId);
                            bidInfo.setUid(uid);
                            rows  = bidInfoMapper.insertSelective(bidInfo);

                            //查看产品是否满标
                            LoanInfo queryLoan = loanInfoMapper.selectByPrimaryKey(loanId);
                            if( queryLoan.getLeftProductMoney().intValue() == 0 ){
                                //满标，更新产品的状态是 1
                                queryLoan.setProductStatus(CommonContants.LOAN_STATUS_MANBIAO);
                                queryLoan.setProductFullTime( new Date());
                                rows = loanInfoMapper.updateByPrimaryKeySelective(queryLoan);
                                if( rows < 1){
                                    throw new RuntimeException("投资-更新产品是满标状态失败");
                                }
                            }

                            result = true;
                        }
                    }
                }
            }
        }
        return result;
    }
}
