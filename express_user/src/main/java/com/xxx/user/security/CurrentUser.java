package com.xxx.user.security;

import com.xxx.core.security.MyAuthenticationUserDetailsService;
import com.xxx.core.security.SecurityUtils;
import com.xxx.utils.AESUtils;

public class CurrentUser {
    /**
     * 生成验证token
     *
     * @param genericLogin
     * @return
     */
    public static String generateAuthToken(GenericLogin genericLogin) {
        try {
//            return AESUtils.encrypt(JSONObject.toJSONString(genericLogin), MyAuthenticationUserDetailsService.TOKEN_KEY);
            return AESUtils.encrypt(genericLogin.toString(), MyAuthenticationUserDetailsService.TOKEN_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取登录用户信息
     *
     * @return
     */
    public static GenericLogin get() {
        return new TokenUserDetails(SecurityUtils.getPrincipal()).getGenericLogin();
    }
}
