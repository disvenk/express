package com.xxx.user.security;

import com.alibaba.fastjson.JSONObject;
import com.xxx.core.security.MyUserDetails;

public class TokenUserDetails {
    private GenericLogin genericLogin;
    private MyUserDetails myUserDetails;

    public TokenUserDetails(MyUserDetails myUserDetails) {
        this.myUserDetails = myUserDetails;
        if (myUserDetails != null)
            parseGenericLogin(myUserDetails.getExpandData());
    }

    private void parseGenericLogin(JSONObject expandData) {
        if (expandData != null)
            this.genericLogin = JSONObject.parseObject(expandData.toString(), GenericLogin.class);
    }

    public GenericLogin getGenericLogin() {
        return genericLogin;
    }
}
