package com.bjpowernode.licai.service.impl;

import com.bjpowernode.commmon.DecimalUtil;
import com.bjpowernode.commmon.PageUtil;
import com.bjpowernode.contants.CommonContants;
import com.bjpowernode.licai.mapper.BidInfoMapper;
import com.bjpowernode.licai.mapper.FinanceAccountMapper;
import com.bjpowernode.licai.mapper.IncomeRecordMapper;
import com.bjpowernode.licai.mapper.LoanInfoMapper;
import com.bjpowernode.licai.model.BidInfo;
import com.bjpowernode.licai.model.FinanceAccount;
import com.bjpowernode.licai.model.IncomeRecord;
import com.bjpowernode.licai.model.LoanInfo;
import com.bjpowernode.licai.model.mix.UserIncomeInfo;
import com.bjpowernode.licai.service.IncomeService;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DubboService(interfaceClass = IncomeService.class,version = "1.0")
public class IncomeServiceImpl implements IncomeService {

    @Resource
    private IncomeRecordMapper recordMapper;

    @Resource
    private FinanceAccountMapper financeAccountMapper;

    @Resource
    private LoanInfoMapper loanInfoMapper;

    @Resource
    private BidInfoMapper bidInfoMapper;

    //某个用户最近的收益记录
    @Override
    public List<UserIncomeInfo> queryRecentlyUserIncome(Integer uid, Integer pageNo, Integer pageSize) {
        List<UserIncomeInfo> list = new ArrayList<>();
        if( uid != null && uid > 0 ){
            pageNo = PageUtil.defaultPageNo(pageNo);
            pageSize = PageUtil.defaultPageSize(pageSize);
            int offset = (pageNo - 1 ) * pageSize;
            list = recordMapper.selectRecentlyInfo(uid,offset,pageSize);
        }
        return list;
    }

    /**生成收益计划
     *
     *  1. 获取满标的产品（ 0-多个）
     *  2. 获取每个产品的 投资记录（ 1-多个）
     *  3. 计算每个投资记录的 ，计划收益
     *  4. 把计划的收益存放到收益表，income_status= 0
     *      *  5. 更新产品的状态为 2
     */
    @Transactional
    @Override
    public synchronized boolean generateIncomePlan() {

        BigDecimal incomeMoney = new BigDecimal("0");
        BigDecimal rate = new BigDecimal("0");
        Double cycle = 0.0;
        Date incomeDate  = null;
        int rows = 0;
        boolean result  = false;

        //1 获取满标的产品
        List<LoanInfo> loanInfoList = loanInfoMapper.selectByStatus(CommonContants.LOAN_STATUS_MANBIAO);
        //2 获取每个产品的 投资记录
        for(LoanInfo loan : loanInfoList){
            List<BidInfo> bidInfoList = bidInfoMapper.selectByLoanId(loan.getId());
            //3.循环投资记录，计算 收益
            for( BidInfo bid: bidInfoList ){

                rate =  DecimalUtil.divide( loan.getRate(), new BigDecimal("100")) ;//利率
                if( loan.getProductType()  == CommonContants.LAON_PRODUCT_TYPE_XINSHOUBAO_0) {
                    // 周期是天
                    //收益  =  投资金额 * 利率 * 周期
                    cycle =   ( loan.getCycle() * 1.0) /365;
                    incomeMoney = DecimalUtil.multiply( bid.getBidMoney(),rate).multiply( new BigDecimal(cycle));
                    //到期时间 = 满标时间 + 产品的周期
                    incomeDate = DateUtils.addDays(loan.getProductFullTime(), loan.getCycle());

                } else {
                    //  周期是月
                    //收益  =  投资金额 * 利率 * 周期
                    cycle = ( loan.getCycle() * 30 * 1.0) / 365;
                    incomeMoney =DecimalUtil.multiply( bid.getBidMoney(), rate ) .multiply( new BigDecimal(cycle));
                    //到期时间 = 满标时间 + 产品的周期
                    incomeDate = DateUtils.addMonths( loan.getProductFullTime(), loan.getCycle());

                }
                //4. 生成收益记录
                IncomeRecord ir  = new IncomeRecord();
                ir.setBidId(bid.getId());
                ir.setBidMoney(bid.getBidMoney());
                ir.setIncomeDate(incomeDate);
                ir.setIncomeMoney(incomeMoney);
                ir.setIncomeStatus( CommonContants.INCOME_STATUS_PLAN );
                ir.setLoanId(loan.getId());
                ir.setUid(bid.getUid());
                rows  = recordMapper.insertSelective(ir);
                if( rows < 0 ){
                    throw new RuntimeException("收益生成计划-创建收益记录失败");
                }
            }
            //5.修改产品的状态是： 满标已生成收益计划
            loan.setProductStatus( CommonContants.LOAN_STATUS_MANBIAO_INCOME);
            rows = loanInfoMapper.updateByPrimaryKeySelective(loan);
            if( rows < 1 ){
                throw new RuntimeException("收益计划生成-更新产品状态失败");
            }
            result = true;
        }

        return result;
    }

    /**收益返还
     *
     * 1. 获取到期的收益计划（定时任务）
     * 2. 把收益和 投资金额都返还到  自己账户
     * 3. 同时修改 收益表 记录的状态是  已经返还

     */
    @Transactional
    @Override
    public synchronized  boolean generateIncomeBack() {

        boolean result = false;
        int rows = 0;
        //1.获取到期的收益计划
        List<IncomeRecord> incomeRecordList = recordMapper.selectExipreIncome();
        //2.遍历数据
        for(IncomeRecord ir : incomeRecordList ){
            //获取  收益  和  投资金额
            //获取资金 账户， 锁定他
            FinanceAccount account = financeAccountMapper.selectUidForUpdate(ir.getUid());
            if( account != null ){
                rows = financeAccountMapper.updateAvaiableMoneyIncome(ir.getUid(),ir.getBidMoney(),ir.getIncomeMoney());
                if( rows < 1){
                    throw new RuntimeException("收益返还-更新资金账户失败");
                }

                // 修改收益的状态是 已返还
                ir.setIncomeStatus(CommonContants.INCOME_STATUS_BACK);
                rows  = recordMapper.updateByPrimaryKeySelective(ir);
                if( rows <1 ){
                    throw new RuntimeException("收益返还-更新收益状态失败");
                }

                result = true;
            }
        }

        return result;
    }
}
