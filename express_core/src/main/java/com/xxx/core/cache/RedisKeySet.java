package com.xxx.core.cache;

import redis.clients.jedis.Jedis;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wanghua on 16/11/2.
 */
class RedisKeySet extends LinkedHashMap<String, String> {
    private static final long serialVersionUID = 1L;
    private int maxSize;

    public RedisKeySet(int initSize) {
        super(initSize, 0.75F, true);
        this.maxSize = initSize;
    }

    //满了移除最旧元素，并更新缓存
    @Override
    public boolean removeEldestEntry(Map.Entry<String, String> eldest) {
        boolean overflow = size() > this.maxSize;
        if (overflow) {
            Jedis jedis = RedisUtils.getJedisPool().getResource();
            try {
                jedis.del(eldest.getKey());
            } finally {
                jedis.close();
            }
        }
        return overflow;  //true 则移除
    }
}


