package com.xxx.core.cache;

import redis.clients.jedis.ShardedJedis;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wanghua on 16/11/11.
 */
public class ShardedJedisSentinelPoolTest {
    public static void main(String[] args) throws Exception {
        Set<String> sentinels = new HashSet<String>();
        sentinels.add("192.168.111.222:26379");
        sentinels.add("192.168.111.222:26380");
        sentinels.add("192.168.111.222:26381");

        ShardedJedisSentinelPool pool = new ShardedJedisSentinelPool(
                Arrays.asList("mymaster-6379", "mymaster-6380"), sentinels,"solution"
        );

        while (true) {
            String ts = new SimpleDateFormat("hh:mm:ss").format(new Date());
            ShardedJedis jedis = pool.getResource();
            try {
                System.out.println(ts + ": hello=" + jedis.get("hello"));
            } catch (Exception e) {
                System.out.println(ts + ": Cannot connect to Redis");
            } finally {
                jedis.close();
            }
            Thread.sleep(500L);
        }
    }
}
