package com.xxx.user.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>Description: 管理缓存</p>
 * 可扩展的功能：当cache到内存溢出时必须清除掉最早期的一些缓存对象，这就要求对每个缓存对象保存创建时间
 * 目前该缓存管理器仅仅用于订单号管理GenerateNumberUtil 与 验证码缓存，如果进行它用，请防止key冲突
 * 后期可改用Redis
 * @author Chen.zm
 */
public class CacheManager {
    private static HashMap cacheMap = new HashMap();

    //单实例构造方法
    private CacheManager() {
        super();
    }

    //获取缓存
    public static Object get(String key){
        return cacheMap.get(key);
    }

    //设置缓存
    public synchronized static boolean put(String key, Object value){
        cacheMap.put(key, value);
        return true;
    }

    //判断是否存在一个缓存
    private synchronized static boolean hasCache(String key) {
        return cacheMap.containsKey(key);
    }

    //清除所有缓存
    public synchronized static void clearAll() {
        cacheMap.clear();
    }

    //清除指定的缓存
    public synchronized static void clearOnly(String key) {
        cacheMap.remove(key);
    }

    //获取缓存中的大小
    public static int getCacheSize() {
        return cacheMap.size();
    }


    //清除某一类特定缓存,通过遍历HASHMAP下的所有对象，来判断它的KEY与传入的TYPE是否匹配
    public synchronized static void clearAll(String type) {
        Iterator i = cacheMap.entrySet().iterator();
        String key;
        ArrayList<String> arr = new ArrayList<String>();
        try {
            while (i.hasNext()) {
                java.util.Map.Entry entry = (java.util.Map.Entry) i.next();
                key = (String) entry.getKey();
                if (key.startsWith(type)) { //如果匹配则删除掉
                    arr.add(key);
                }
            }
            for (int k = 0; k < arr.size(); k++) {
                clearOnly(arr.get(k));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    //获取指定的类型的大小
    public static int getCacheSize(String type) {
        int k = 0;
        Iterator i = cacheMap.entrySet().iterator();
        String key;
        try {
            while (i.hasNext()) {
                java.util.Map.Entry entry = (java.util.Map.Entry) i.next();
                key = (String) entry.getKey();
                if (key.indexOf(type) != -1) { //如果匹配则删除掉
                    k++;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return k;
    }

    //获取缓存对象中的所有键值名称
    public static ArrayList<String> getCacheAllkey() {
        ArrayList a = new ArrayList();
        try {
            Iterator i = cacheMap.entrySet().iterator();
            while (i.hasNext()) {
                java.util.Map.Entry entry = (java.util.Map.Entry) i.next();
                a.add((String) entry.getKey());
            }
        } catch (Exception ex) {} finally {
            return a;
        }
    }

    //获取缓存对象中指定类型 的键值名称
    public static ArrayList<String> getCacheListkey(String type) {
        ArrayList a = new ArrayList();
        String key;
        try {
            Iterator i = cacheMap.entrySet().iterator();
            while (i.hasNext()) {
                java.util.Map.Entry entry = (java.util.Map.Entry) i.next();
                key = (String) entry.getKey();
                if (key.indexOf(type) != -1) {
                    a.add(key);
                }
            }
        } catch (Exception ex) {} finally {
            return a;
        }
    }

}