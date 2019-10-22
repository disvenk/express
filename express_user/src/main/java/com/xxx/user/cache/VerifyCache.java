package com.xxx.user.cache;

import com.xxx.core.cache.RedisUtils;

import java.util.Date;

/**
 * Verify缓存
 */
public class VerifyCache {
    private static String KEY_PREFIX = "VerifyCache";

    /**
     * 缓存发送的验证码（缓存5分钟）
     *
     * @param phone
     * @param verificationCode
     */
    public static void putVerificationCode(String phone, Object verificationCode) {
        String key = KEY_PREFIX + phone;
        RedisUtils.setex(key, 300, verificationCode);
        RedisUtils.setex(key + "Time", 300, new Date());
    }

    /**
     * 获取缓存的验证码
     *
     * @param phone
     * @return
     */
    public static String getVerificationCode(String phone) {
        String key = KEY_PREFIX + phone;
        return RedisUtils.get(key);
    }

    /**
     * 获取创建验证码的时间
     *
     * @param phone
     * @return
     */
    public static Date getVerificationCodeTime(String phone) {
        String key = KEY_PREFIX + phone;
        return RedisUtils.get(key + "Time");
    }

    /**
     * 删除缓存的验证码
     *
     * @param phone
     * @return
     */
    public static Long deleteVerificationCode(String phone) {
        String key = KEY_PREFIX + phone;
        return RedisUtils.del(key);
    }
}
