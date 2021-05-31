/**
 * @项目名：crm-project
 * @创建人： Administrator
 * @创建时间： 2020-05-21
 * @公司： www.bjpowernode.com
 * @描述：TODO
 */
package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.DicType;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicTypeService;
import com.bjpowernode.crm.settings.service.DicValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * <p>NAME: DicValueController</p>
 * @author Administrator
 * @date 2020-05-21 09:30:10
 * @version 1.0
 */
@Controller
public class DicValueController {

    @Autowired
    private DicValueService dicValueService;

    @Autowired
    private DicTypeService dicTypeService;

    @RequestMapping("/settings/dictionary/value/index.do")
    public String index(Model model){
        //调用service方法，查询所有的数据字典值
        List<DicValue> dicValueList=dicValueService.queryAllDicValues();
        //把数据保存到request中
        model.addAttribute("dicValueList",dicValueList);
        //请求转发
        return "settings/dictionary/value/index";
    }

    @RequestMapping("/settings/dictionary/value/toSave.do")
    public String toSave(Model model){
        //调用service层方法，查询所有的数据字典类型
        List<DicType> dicTypeList=dicTypeService.queryAllDicTypes();
        //把dicTypeList保存到request中
        model.addAttribute("dicTypeList",dicTypeList);
        //请求转发
        return "settings/dictionary/value/save";
    }

    @RequestMapping("/settings/dictionary/value/saveCreateDicValue.do")
    public @ResponseBody Object saveCreateDicValue(DicValue dicValue){
        //封装参数
        dicValue.setId(UUIDUtils.getUUID());

        ReturnObject returnObject=new ReturnObject();
        try {
            //调用service层方法，保存数据
            int ret = dicValueService.saveCreateDicValue(dicValue);
            if(ret>0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            }else{
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试....");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试....");
        }
        return returnObject;
    }

    @RequestMapping("/settings/dictionary/value/deleteDicValueByIds.do")
    public @ResponseBody  Object deleteDicValueByIds(String[] id){
        ReturnObject returnObject=new ReturnObject();
        try {
            //调用service层方法，删除数据字典值
            int ret=dicValueService.deleteDicValueByIds(id);
            if(ret>0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            }else{
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试....");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试....");
        }
        return returnObject;
    }

    @RequestMapping("/settings/dictionary/value/editDicValue.do")
    public String editDicValue(String id,Model model){
        //调用service层方法，查询数据字典值
        DicValue dicValue=dicValueService.queryDicValueById(id);
        //把数据保存到request中
        model.addAttribute("dicValue",dicValue);
        //请求转发
        return "settings/dictionary/value/edit";
    }

    @RequestMapping("/settings/dictionary/value/saveEditDicValue.do")
    public @ResponseBody Object saveEditDicValue(DicValue dicValue){
        ReturnObject returnObject=new ReturnObject();
        try {
            //调用service层方法，修改数据字典类型
            int ret=dicValueService.saveEditDicValue(dicValue);
            if(ret>0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            }else{
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试...");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }
        return returnObject;
    }
}
