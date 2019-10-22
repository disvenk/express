package com.xxx.user.cache;

import com.xxx.user.utils.CacheManager;

import java.util.Date;

/**
 * Verify缓存
 */
public class VerifyManagerCache {
    private static String KEY_PREFIX = "VerifyCache";

    /**
     * 缓存发送的验证码（缓存5分钟）
     *
     * @param phone
     * @param verificationCode
     */
    public static void putVerificationCode(String phone, Object verificationCode) {
        String key = KEY_PREFIX + phone;
        CacheManager.put(key, verificationCode);
        CacheManager.put(key + "Time", new Date());
    }

    /**
     * 获取缓存的验证码
     *
     * @param phone
     * @return
     */
    public static String getVerificationCode(String phone) {
        String key = KEY_PREFIX + phone;
        return CacheManager.get(key) == null ? null : CacheManager.get(key).toString();
    }

    /**
     * 获取创建验证码的时间
     *
     * @param phone
     * @return
     */
    public static Date getVerificationCodeTime(String phone) {
        String key = KEY_PREFIX + phone;
        return CacheManager.get(key + "Time") == null ? null : (Date) CacheManager.get(key + "Time");
    }

    /**
     * 删除缓存的验证码
     *
     * @param phone
     * @return
     */
    public static void deleteVerificationCode(String phone) {
        String key = KEY_PREFIX + phone;
        CacheManager.clearOnly(key);
    }
}
