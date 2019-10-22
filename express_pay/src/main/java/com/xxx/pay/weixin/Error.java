package com.xxx.pay.weixin;

/**
 * Created by wangh on 2016/7/1.
 */
public class Error {
    private Integer errcode;
    private String errmsg;

    public  Error(){

    }
    public Error(Integer errcode, String errmsg) {
        this.errcode = errcode;
        this.errmsg = errmsg;
    }

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}
