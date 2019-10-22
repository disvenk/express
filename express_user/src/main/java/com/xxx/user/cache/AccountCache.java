package com.xxx.user.cache;

import com.xxx.core.cache.Configs;
import com.xxx.core.cache.RedisUtils;
import com.xxx.user.security.GenericLogin;

/**
 * Created by wanghua on 17/2/10.
 */
public class AccountCache {
    private static String KEY_PREFIX = "AccountCache";
    private static int expireSeconds = Configs.getRedisStoreExpire();

    /**
     * 设置登录数据
     *
     * @param userId
     * @param userType
     * @param userData
     */
    public static void putLoginData(long userId, int userType, GenericLogin userData) {
        String key = KEY_PREFIX + String.valueOf(userId) + "-" + Integer.valueOf(userType);
        RedisUtils.setex(key, expireSeconds, userData);
    }


//    /**
//     * 获取登录数据
//     *
//     * @param userId
//     * @param userType
//     * @return
//     */
//    public static Store getLoginData(long userId, int userType) {
//        String key = KEY_PREFIX + String.valueOf(userId) + "-" + Integer.valueOf(userType);
//        RedisUtils.expire(key, expireSeconds);
//        return RedisUtils.get(key);
//    }
}
