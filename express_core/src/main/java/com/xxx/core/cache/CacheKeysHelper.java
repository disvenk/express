package com.xxx.core.cache;

import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by wanghua on 16/11/8.
 */
public class CacheKeysHelper {
    private static final String SPRING_CACHE_KEYS = "SpringCacheKeys";
    private static final String PRESENT = new String();
    private static final int maxElements = 10000;  //单个cache存储的key最大数量

    //keys必须可持久化，否则重新启动，内存中keys就丢了，即使redis中存有数据也要重新从数据库取一下。因此 keys 中数据不会过期，仅在编辑、修改、删除时被删除。
    //这里使用 key 为 SPRING_CACHE_KEYS 的 redis来存储所有的key，重启后以便重新加载他们。
    public static Map<String, String> loadSpringCacheKeysFromRedis(String name) {
        Map<String, String> keys = Collections.synchronizedMap(new RedisKeySet(maxElements));
        Jedis jedis = RedisUtils.getJedisPool().getResource();
        try {
            Set<String> set = jedis.smembers(getCacheKey(name));
            Iterator<String> iter = set.iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                keys.put(key, PRESENT);
            }
        } finally {
            jedis.close();
        }

        return keys;
    }

    public static void saveCacheKeys(String name, String ckey) {
        Jedis jedis = RedisUtils.getJedisPool().getResource();
        try {
            jedis.sadd(getCacheKey(name), ckey);
        } finally {
            jedis.close();
        }
    }

    public static String getCacheKey(String name) {
        if (StringUtils.isBlank(name))
            throw new RuntimeException("@Cacheable value值不能为空！");
        return SPRING_CACHE_KEYS + "_" + name + ",";  //最后都加一个英文逗号，以方便正则表达式匹配
    }

    public static Set<String> findCacheKeysByEntity(String entityName) {
        if (StringUtils.isBlank(entityName))
            throw new RuntimeException("@Cacheable value值不能为空！");
        String pattern = MessageFormat.format("{1}*[,_]{0}[,]*", entityName,SPRING_CACHE_KEYS);

        Jedis jedis = RedisUtils.getJedisPool().getResource();
        try {
            return jedis.keys(pattern);
        } finally {
            jedis.close();
        }
    }

    public static Set<String> getCacheKeyValues(String cacheKey) {
        Jedis jedis = RedisUtils.getJedisPool().getResource();
        try {
            return jedis.smembers(cacheKey);
        }finally {
            jedis.close();
        }
    }
}
