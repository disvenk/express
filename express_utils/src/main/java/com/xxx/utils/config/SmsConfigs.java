package com.xxx.utils.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;


public class SmsConfigs {
    private static Configuration configs;
    private static String apiId;
    private static String apiKey;

    static {
        //sms.properties 文件在 kaa_core 模块中
        init("sms.properties");
    }

    // 根据文件名读取配置文件，文件后缀名必须为.properties
    public synchronized static void init(String filePath) {
        if (configs != null)
            return;

        try {
            configs = new PropertiesConfiguration(filePath);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

        if (configs == null)
            throw new IllegalStateException("can`t find file by path:" + filePath);

        apiId = configs.getString("apiId");
        apiKey = configs.getString("apiKey");
    }

    public static String getApiId() {
        return apiId;
    }

    public static void setApiId(String apiId) {
        SmsConfigs.apiId = apiId;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static void setApiKey(String apiKey) {
        SmsConfigs.apiKey = apiKey;
    }
}
