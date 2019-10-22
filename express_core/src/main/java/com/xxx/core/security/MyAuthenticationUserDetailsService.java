package com.xxx.core.security;

import com.alibaba.fastjson.JSONObject;
import com.xxx.utils.AESUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MyAuthenticationUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
//    public static final String TOKEN_KEY = "wanghuabodong507";
    public static final String TOKEN_KEY = "chenzhimingkaa00";

    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
        String principal = (String) token.getPrincipal();  //获取使用者信息

        if (!StringUtils.isEmpty(principal)) {
            //TODO 检查token是否过期（redis缓存中不存在则视为过期），如果存在则再延长N天

            try {
                principal = AESUtils.decrypt(principal, TOKEN_KEY);
                JSONObject jToken = JSONObject.parseObject(principal);
                Integer userId = jToken.getInteger("userId");
                Integer userType = jToken.getInteger("userType");
                String userName = jToken.getString("userName");
                //String userType = jToken.getString("userType");
                //Integer brandId = jToken.getInteger("brandId");
                //Integer regionalManagerId = jToken.getInteger("regionalManagerId");
                //Integer storeId = jToken.getInteger("storeId");
                //Integer beauticianId = jToken.getInteger("beauticianId");

                return new MyUserDetails(userName, "", true, true, true, true, getAuthorities(1), userId, jToken,userType);
            } catch (Exception e) {
                e.printStackTrace();
                throw new UsernameNotFoundException("");
            }
        } else {
            //抛出 UsernameNotFoundException 异常，会进入 RestAuthenticationEntryPoint；如果 return null，是不会进入 RestAuthenticationEntryPoint 的。
            throw new UsernameNotFoundException("");
        }
    }

    @SuppressWarnings("deprecation")
    public Collection<GrantedAuthority> getAuthorities(Integer access) {
        List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>(2);
        authList.add(new SimpleGrantedAuthority("ROLE_USER"));  // 所有的用户默认拥有ROLE_USER权限
        if (access.compareTo(1) == 0) {  // 如果参数access为1.则拥有ROLE_ADMIN权限
            authList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        //返回权限列表，像管理员账号，zhangsan。返回的authList的值为 :[ROLE_USER, ROLE_ADMIN]，而普通用户返回的值为[ROLE_USER]
        return authList;
    }
}