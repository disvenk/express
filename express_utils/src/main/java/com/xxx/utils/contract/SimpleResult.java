package com.xxx.utils.contract;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Created by wangh on 2015/12/14.
 */
public class SimpleResult {
    public boolean success;
    public String message;
    public String code;        //用于ajax返回策略。302-跳到登录页
    public String returnUrl;   //登录后返回到这里指定的页面
    public String redirectUrl; //重定向到这里指定的页面

    public SimpleResult(){}

    public SimpleResult(boolean success, String message) {
        this(success, message, null,null);
    }

    public SimpleResult(boolean success, String message, String code, String returnUrl) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.returnUrl=returnUrl;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteNullStringAsEmpty);
    }
}
