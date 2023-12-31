package com.bjpowernode.crm.commons.domain;

/**
 * 公共类：公共的JSON返回对象类
 */
public class ReturnObject {

    private String code;//处理成功--1，失败--0
    private String message;//提示信息
    private Object retData;//返回其他数据

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getRetData() {
        return retData;
    }

    public void setRetData(Object retData) {
        this.retData = retData;
    }
}
