package com.xxx.core.security;

import com.alibaba.fastjson.JSONObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class MyUserDetails extends User {
    private Integer userId;
    private Integer userType;
    private JSONObject expandData;

    public MyUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Integer userId, JSONObject expandData) {
        super(username, password, authorities);
        this.userId = userId;
        this.expandData = expandData;
    }

    public MyUserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, Integer userId, JSONObject expandData,Integer userType) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
        this.expandData = expandData;
        this.userType=userType;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public JSONObject getExpandData() {
        return expandData;
    }

    public void setExpandData(JSONObject expandData) {
        this.expandData = expandData;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }
}
