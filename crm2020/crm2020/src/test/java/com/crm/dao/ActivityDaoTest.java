package com.crm.dao;

import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.mapper.ActivityMapper;
import com.crm.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;

public class ActivityDaoTest extends BaseTest {
	@Autowired
	private ActivityMapper activityMapper;

	/**
	 * 保存创建的市场活动
	 */
	@Test
	public void testInsertActivity(){
		Activity activity=new Activity();
		activity.setId("abcdefg123");
		activity.setOwner("cjc");
		activityMapper.insertActivity(activity);

	}

	//	根据条件分页查询市场活动
	@Test
	public void testSelectActivityForPageByCondition(){
		HashMap map=new HashMap();
		map.put("name","吕布");
		map.put("beginNo",0);
		map.put("pageSize",2);
		List<Activity> activities=activityMapper.selectActivityForPageByCondition(map);
		System.out.println(activities.size());
	}

	//根据条件查询市场活动总条数
	@Test
	public void selectCountOfActivityByCondition(){
		HashMap map=new HashMap();
		long count=activityMapper.selectCountOfActivityByCondition(map);
		System.out.println(count);
	}

	//测试没有绑定cludId的市场活动
	@Test
	public void searchActivityNoBoundById(){
		HashMap map=new HashMap();
	    map.put("activityName","家具");
		map.put("clueId","757f3c29fab94ba2b0287b3dee842e9b");
		List<Activity>  aList=activityMapper.searchActivityNoBoundById(map);
		System.out.println(aList.size());
	}

}
