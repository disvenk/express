package com.xxx.core.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import java.util.Collection;

/**
 * Created by wanghua on 16/11/7.
 */
public class RedisCacheManager extends AbstractCacheManager {
    private static int expire;

    public RedisCacheManager() {
    }

    public RedisCacheManager(int expire) {
        this.expire = expire;
    }

    public static void setExpire(int expire) {
        RedisCacheManager.expire = expire;
    }

    // AbstractCacheManager.afterPropertiesSet不允许loadCaches返回空，所以覆盖掉
    @Override
    public void afterPropertiesSet() {
    }

    protected Collection<? extends Cache> loadCaches() {
        return null;
    }

    // 根据名称获取cache，对应注解里的value如notice_cache，没有就创建并加入cache管理
    public Cache getCache(String name) {
        Cache cache = super.getCache(name);
        if (cache == null) {
            cache = new RedisCache(name, expire);
            super.addCache(cache);
        }
        return cache;
    }
}
