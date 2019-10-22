package com.xxx.core.security;

import com.xxx.utils.FileReaderUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by wanghua on 17/2/9.
 */
public class SecurityUtils {
    /**
     * 获得当前登录用户信息
     *
     * @return MyUserDetails
     */
    public static MyUserDetails getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (authentication == null) return null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof String) {  //匿名用户
            return null;
        } else {
            return (MyUserDetails) principal;
        }
    }

    /**
     * 获得当前登录用户所拥有的所有权限
     *
     * @return Collection
     */
    public static Collection<? extends GrantedAuthority> getAuthorities() {
        if (getPrincipal() == null) return null;
        Collection<? extends GrantedAuthority> authorities = getPrincipal().getAuthorities();
        return authorities;
    }


    /**
     * 获取所有permitAll路径
     *
     * @return
     */
    public static List<String> getPermitAllRequest() {
        List<String> permitAllRequests = new ArrayList<>();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources("classpath*:/permitAllRequest-*");
            for (Resource resource : resources)
                permitAllRequests.addAll(FileReaderUtils.readFileByLines(resource.getInputStream()));
            //Assert.assertTrue(resources.length > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return permitAllRequests;
    }
}
