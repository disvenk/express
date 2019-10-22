package com.xxx.utils.cache;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangh on 2015/12/15.
 */
public class InfinispanUtils {
    private static DefaultCacheManager defaultCacheManager;

    private static DefaultCacheManager getCacheManager() throws IOException {
        if (defaultCacheManager == null)
            defaultCacheManager = new DefaultCacheManager("infinispan.xml");
        return defaultCacheManager;
    }

    //获取默认Local缓存
    public static Cache<Object, Object> getCache() throws IOException {
        return getCacheManager().getCache();
    }

    //获取指定名称的缓存
    public static Cache<Object, Object> getCache(String name) throws IOException {
        return getCacheManager().getCache(name);
    }

    public static void main(String[] args) throws Exception {
        Cache<Object, Object> cache = InfinispanUtils.getCache("user");
        cache.put("name", "王华", 10, TimeUnit.SECONDS);
        System.out.println("缓存值：" + cache.get("name"));
        Thread.sleep(15000);
        System.out.println("15秒过后，缓存值：" + cache.get("name"));
    }
}
