package com.xxx.core.cache;

import com.xxx.utils.ByteUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.Set;

/**
 * Created by wanghua on 16/11/7.
 * 已经过并发测试。
 */
public class RedisCache implements Cache {
    private static final String PRESENT = new String();

    // 默认过期时间
    private static int expire;
    private String name;  //对应 @Cacheable 中的 value，即缓存名

    /*
      存储key的集合，使用LinkedHashMap实现。
      keys必须可持久化，否则重新启动，内存中keys就丢了，即使redis中存有数据也要重新从数据库取一下。因此 keys 中数据不会过期，仅在编辑、修改、删除时被删除。
    */
    Map<String, String> keys;
    //private KeySet keys;

    public RedisCache() {
    }


    public RedisCache(String name, int expire) {
        //this();
        this.name = name;
        this.expire = expire;
        //this.keys = new KeySet(maxElements);
        this.keys = CacheKeysHelper.loadSpringCacheKeysFromRedis(name);  //持久化 keys
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return RedisUtils.getJedisPool().getResource();
    }

    // ckey是key+cacheName作为前缀，也是最终存入缓存的key。如果返回null，则等同于 @Cacheable 不起作用。
    @Override
    public ValueWrapper get(Object key) {
        String ckey = toStringWithCacheName(key);
        if (keys.containsKey(ckey)) {
            Object value = null;
            Jedis jedis = RedisUtils.getJedisPool().getResource();
            try {
                byte[] bytes = ckey.getBytes();
                value = jedis.get(bytes);
                //System.out.println("剩余时间1："+shardedJedis.ttl(bytes));
                jedis.expire(bytes, expire);  //继续延长过期时间
                //System.out.println("剩余时间2："+shardedJedis.ttl(bytes));
            } finally {
                jedis.close();
            }

            if (value != null) {
                try {
                    value = ByteUtils.toObject((byte[]) value);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return value != null ? new SimpleValueWrapper(value) : null;
        } else {
            return null;
        }
    }

    // 将ckey加入key集合并将ckey-value存入缓存
    @Override
    public void put(Object key, Object value) {
        String ckey = toStringWithCacheName(key);
        Jedis jedis = RedisUtils.getJedisPool().getResource();
        try {
            keys.put(ckey, PRESENT);
            CacheKeysHelper.saveCacheKeys(name, ckey);

            byte[] bytes = null;
            try {
                bytes = ByteUtils.toByteArray(value);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (bytes != null) {
                jedis.setex(ckey.getBytes(), expire, bytes);
            }
        } finally {
            jedis.close();
        }
    }

    // 从keys集合清除ckey，并从缓存清除
    @Override
    public void evict(Object key) {
        String ckey = toStringWithCacheName(key);
        keys.remove(ckey);

        Jedis jedis = RedisUtils.getJedisPool().getResource();
        try {
            jedis.del(ckey.getBytes());
            jedis.srem(CacheKeysHelper.getCacheKey(name), ckey);
        } finally {
            jedis.close();
        }
    }

    //@Cacheable的value值+hascode()值
    private String toStringWithCacheName(Object obj) {
        return name + "." + String.valueOf(obj);
    }

    // 遍历清除
    @Override
    public void clear() {
        Jedis jedis = RedisUtils.getJedisPool().getResource();
        //try {
        //    for (String ckey : keys.keySet()) {
        //        jedis.del(ckey.getBytes());
        //       jedis.srem(CacheKeysHelper.getKey(name), ckey);
        //    }
        //} finally {
        //    //jedis.close();
        //}
        //keys.clear();
        //jedis.del(CacheKeysHelper.getKey(name));

        try {
            //注意：无法删除已经实例化 RedisCache 中的 keys，但这没关系，get 时如果redis中没有数据，会再次put。
            for (String entityName : name.split(",")) {
                if (StringUtils.isBlank(entityName)) continue;
                Set<String> cacheKeyList = CacheKeysHelper.findCacheKeysByEntity(entityName);
                for (String cacheKey : cacheKeyList) {
                    Set<String> values = CacheKeysHelper.getCacheKeyValues(cacheKey);
                    if (values == null) continue;
                    for (String ckey : values) {
                        Long l = jedis.del(ckey.getBytes());  //成功删除返回1
                    }

                    Long l = jedis.del(cacheKey);
                }
            }
            keys.clear();
        }finally {
            jedis.close();
        }
    }

    @Override
    public <T> T get(Object o, Class<T> aClass) {
        return null;
    }

    @Override
    public ValueWrapper putIfAbsent(Object o, Object o1) {
        return null;
    }

    public Map<String, String> getKeys() {
        return this.keys;
    }
}
