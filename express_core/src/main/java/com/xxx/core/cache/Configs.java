package com.xxx.core.cache;

import com.xxx.core.spring.SpringContext;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class Configs {
    private static Configuration configs;
    private static final int redisStoreExpire = (int) SpringContext.getApplicationContext().getBean("redisStoreExpire");
    private static final int redisQueryExpire = (int) SpringContext.getApplicationContext().getBean("redisQueryExpire");

    static {
        //init("redis.properties");
    }

    // 根据文件名读取配置文件，文件后缀名必须为.properties
    private static void init(String filePath) {
        try {
            configs = new PropertiesConfiguration(filePath);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

        if (configs == null) {
            throw new IllegalStateException("can`t find file by path:" + filePath);
        }

        //payAppid = configs.getString("payAppid");
    }

    public static int getRedisStoreExpire() {
        return redisStoreExpire;
    }

    public static int getRedisQueryExpire() {
        return redisQueryExpire;
    }
}
