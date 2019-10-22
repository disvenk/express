package com.xxx.user.utils;

import com.xxx.core.cache.RedisUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


public class SessionUtils {

    public static final String DATA = "DATA";
    public static final String USER_ID = "USER_ID";
    public static final String LOGIN_TYPE = "LOGIN_TYPE";
    public static final String SCHOOL_ID = "SCHOOL_ID";

    public static final String OPEN_ID = "OPEN_ID";



    public static void setSession(HttpServletResponse response,String cookieName,int expireSeconds,Object object){
        String cacheKey = "";
        cacheKey = UUID.randomUUID().toString();
        try {
            RedisUtils.setex(cacheKey, expireSeconds, object);
            Cookie cookie = new Cookie(cookieName,cacheKey);
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getSession(HttpServletRequest request, String cookieName){
        if (request == null) return null;
        Cookie[] cookies = request.getCookies();
        Object object = new Object();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    String key = cookie.getValue();
                    object = RedisUtils.get(key);
                    return object;
                }
            }
        }
        return null;
    }

}
