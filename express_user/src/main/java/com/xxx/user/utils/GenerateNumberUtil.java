package com.xxx.user.utils;

import com.xxx.core.cache.RedisUtils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class GenerateNumberUtil {

    private static String KEY_PREFIX = "OrderNo";



//    /**
//     * 获取缓存数据
//     */
//    private static StringBuilder generateCacheNumber(String number){
//        Object var = CacheManager.get(number);
//        if (var == null) var = 1;
//        CacheManager.put(number, (int)var + 1);
//        return new StringBuilder(var.toString());
//    }

    /**
     * 获取缓存数据  30天
     */
    private static StringBuilder generateCacheNumber(String number){
        String key = KEY_PREFIX + number;
        Object var = RedisUtils.get(key);
        if (var == null) var = 1;
        RedisUtils.set(key, (int)var + 1);
        return new StringBuilder(var.toString());
    }


    /**
     * 生成  6位日期
     */
    private static StringBuilder generateDateNumber(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        return new StringBuilder(sdf.format(new Date()));
    }

    /**
     * 生成  8位时间戳
     */
    private static StringBuilder generateTimeNumber(){
        return new StringBuilder((new Date().getTime()/1000+"").substring(2));
    }

    /**
     * 生成寄件订单号
     *
     */
    public static String generateSendExpressNumber() {
        return generateDateNumber().append(1).append(generateTimeNumber()).toString();
    }

    /**
     * 生成采购订单号
     * 订单类型:2
     */
    public static String generateStoreSuplierNumber() {
        return generateDateNumber().append(2).append(generateTimeNumber()).toString();
    }
    /**
     * 关联销售订单（通过时间戳匹配）
     */
    public static String generateStoreSuplierNumber(String number) {
        return generateDateNumber().append(2).append(number.substring(6,14)).toString();
    }


    /**
     * 生成供应商订单号
     * 订单类型:3
     *
     */
    public static String generateSuplierNumber() {
        return generateDateNumber().append(3).append(generateTimeNumber()).toString();
    }
    /**
     * 需要关联采购订单（通过时间戳匹配）
     * 追加多订单关联数量
     */
    public static String generateSuplierNumber(String number) {
        return generateDateNumber().append(3).append(number.substring(6,14))
                .append(generateCacheNumber(number.substring(6,14))).toString();
    }


    public static void main(String[] args) {
        String a = generateSendExpressNumber();
        System.out.println("a: " + a);
        System.out.println("b: " + generateStoreSuplierNumber());
        System.out.println("c: " + generateStoreSuplierNumber("171102012345671"));
        System.out.println("d: " + generateSuplierNumber("171102012345671"));
        System.out.println("d: " + generateSuplierNumber("171102012345671"));
        System.out.println("d: " + generateSuplierNumber("171102012345671"));
        System.out.println("d: " + generateSuplierNumber("171102012345671"));
    }

}
