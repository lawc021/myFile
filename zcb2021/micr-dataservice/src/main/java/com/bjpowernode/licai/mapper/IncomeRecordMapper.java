package com.bjpowernode.licai.mapper;

import com.bjpowernode.licai.model.IncomeRecord;
import com.bjpowernode.licai.model.mix.UserIncomeInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IncomeRecordMapper {

    // //某个用户最近的收益记录
    List<UserIncomeInfo> selectRecentlyInfo(@Param("uid") Integer uid,
                                            @Param("offset") Integer offset,
                                            @Param("rows") Integer rows);

    // 查询 到期的收益计划
    List<IncomeRecord> selectExipreIncome();



    int deleteByPrimaryKey(Integer id);

    int insert(IncomeRecord record);

    int insertSelective(IncomeRecord record);

    IncomeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IncomeRecord record);

    int updateByPrimaryKey(IncomeRecord record);
}