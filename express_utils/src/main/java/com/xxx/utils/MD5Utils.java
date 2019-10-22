package com.xxx.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by wanghua on 17/2/24.
 */
public class MD5Utils {
    /**
     * MD5加密
     *
     * @param data
     * @return
     */
    public static String md5Hex(String data) {
        return DigestUtils.md5Hex(data);
    }
}
