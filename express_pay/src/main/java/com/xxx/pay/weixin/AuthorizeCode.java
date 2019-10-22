package com.xxx.pay.weixin;

/**
 * Created by wangh on 2016/7/1.
 */
public class AuthorizeCode {
    private String code;
    private String state;

    public AuthorizeCode(String code, String state) {
        this.code = code;
        this.state = state;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
