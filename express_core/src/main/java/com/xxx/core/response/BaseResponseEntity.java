package com.xxx.core.response;

/**
 * Created by wanghua on 17/1/19.
 */
public class BaseResponseEntity {
    public int stateCode;    //业务响应状态，非HTTP状态码，HTTP状态码在 ResponseEntity 中指定
    public String message;   //消息

    public BaseResponseEntity() {

    }

    public BaseResponseEntity(int stateCode, String message) {
        this.stateCode = stateCode;
        this.message = message;
    }

    public int getStateCode() {
        return stateCode;
    }

    public void setStateCode(int stateCode) {
        this.stateCode = stateCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
