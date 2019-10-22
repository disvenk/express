package com.xxx.utils;

/**
 * Created by wanghua on 17/3/10.
 */
public class RandomUtils {
    /**
     * 随机生成一个固定长度的数字字符串
     *
     * @param length
     * @return
     */
    public static String randomFixedLength(int length) {
        String validateCode = "";
        for (int i = 0; i < length; i++)
            validateCode += (int) (Math.random() * 10);
        return validateCode;
    }

    public static void main(String[] args) {
        System.out.println(RandomUtils.randomFixedLength(4));
        System.out.println(RandomUtils.randomFixedLength(4));
    }
}
