package com.xxx.core.cache;

import com.xxx.core.spring.SpringContext;
import com.xxx.utils.ByteUtils;
import com.xxx.utils.LogUtils;
import redis.clients.jedis.*;

/**
 * 注意：jedisPool、shardedJedisPool都是线程安全的，可以用于 static。shardedJedisSentinelPool未经测试。
 */
public class RedisUtils {
    private static Jedis jedis;
    private static JedisPool jedisPool;
    private static ShardedJedisPool shardedJedisPool;
    private static ShardedJedisSentinelPool shardedJedisSentinelPool;  //暂不用，需要将 jedis.subscribe() 拆分出去独立部署才能用

    static {
        try {
            jedisPool = SpringContext.getApplicationContext().getBean(JedisPool.class);
            //shardedJedisPool = SpringContext.getApplicationContext().getBean(ShardedJedisPool.class);
            //shardedJedisSentinelPool = SpringUtil.getApplicationContext().getBean(ShardedJedisSentinelPool.class);
        } catch (Throwable e) {
            throw new RuntimeException("获取 JedisPool Bean失败！");
        }
    }

    public static JedisPool getJedisPool() {
//        if (jedis == null) {
//            jedis = jedisPool.getResource();  //每次使用 jedis 都调用 getResource 开销会比较大
//        } else if (!jedis.isConnected()) {    //加这个判断，执行100次get操作，会增加1秒的耗时，可以接受
//            jedis = jedisPool.getResource();
//        }
        return jedisPool;
    }

    /**
     * @Description: 缓存数据 同时设置过期时间
     * @param: [key, seconds, value]
     * @return: void
     */
    public static void setex(String key, int seconds, Object value) {
        byte[] bytes = null;
        try {
            bytes = ByteUtils.toByteArray(value);
        } catch (Exception ex) {
            LogUtils.errorFormat(ex, "RedisUtils", "setex");
        }
        if (bytes != null) {
            Jedis jedis = getJedisPool().getResource();
            try {
                jedis.setex(key.getBytes(), seconds, bytes);
            } finally {
                jedis.close();
            }
        }
    }

    /**
     * @Description: 缓存数据
     * @param: [key, value]
     * @return: void
     */
    public static void set(String key, Object value) {
        byte[] bytes = null;
        try {
            bytes = ByteUtils.toByteArray(value);
        } catch (Exception ex) {
            LogUtils.errorFormat(ex, "RedisUtils", "set");
        }
        if (bytes != null) {
            Jedis jedis = getJedisPool().getResource();
            try {
                jedis.set(key.getBytes(), bytes);
            } finally {
                jedis.close();
            }
        }
    }


    /**
     * @Description: 获取缓存数据
     * @param: [key]
     * @return: T
     */
    public static <T> T get(String key) {
        T t = null;
        Jedis jedis = getJedisPool().getResource();
        try {
            byte[] bytes = jedis.get(key.getBytes());
            if (bytes != null) {
                try {
                    t = (T) ByteUtils.toObject(bytes);
                } catch (Exception ex) {
                    LogUtils.errorFormat(ex, "RedisUtils", "get");
                }
            }
        } finally {
            jedis.close();
        }
        return t;
    }

    /**
     * @Description: 为缓存的数据设置过期时间
     * @param: [key, seconds]
     * @return: void
     */
    public static void expire(String key, int seconds) {
        Jedis jedis = getJedisPool().getResource();
        try {
            jedis.expire(key.getBytes(), seconds);
        } finally {
            jedis.close();
        }
    }

    /**
     * @Description: 删除缓存
     * @param: [key]
     * @return: java.lang.Long
     */
    public static Long del(String... key) {
        Jedis jedis = getJedisPool().getResource();
        try {
            return jedis.del(key);
        } finally {
            jedis.close();
        }
    }

    public static void main(String[] args) {
//        Jedis client = new Jedis("r-bp1980580e5b4df4.redis.rds.aliyuncs.com", 6379);
//        client.auth("Kaaredis426");
//        Jedis client = new Jedis("42.96.197.253", 6379);
//        client.auth("solution");
        Jedis client = new Jedis("47.97.187.92", 6379, 100000);
        client.auth("kaa426");
        // 执行set指令
        String result = client.set("key-string", "Hello, Redis!");
        System.out.println(String.format("set指令执行结果:%s", result));
        // 执行get指令
        String value = client.get("key-string");
        System.out.println(String.format("get指令执行结果:%s", value));
    }
}
