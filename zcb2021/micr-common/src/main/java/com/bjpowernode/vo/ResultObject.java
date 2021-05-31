package com.bjpowernode.vo;

import com.bjpowernode.contants.Code;

//ajax请求的返回结果
public class ResultObject {
    private Integer code; //返回状态码
    private String message;//返回状态码的解读
    private Object data; //数据

    public ResultObject() {
    }

    public ResultObject(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    //创建一个 默认错误的 Ro对象
    public static ResultObject error(String msg){
        ResultObject ro  = new ResultObject(Code.UNKNOWN_ERROR,msg,"");
        return ro;
    }

    //创建一个 成功的 Ro对象
    public static ResultObject success(String msg){
        ResultObject ro  = new ResultObject(Code.SUCCESS_OK,msg,"");
        return ro;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
