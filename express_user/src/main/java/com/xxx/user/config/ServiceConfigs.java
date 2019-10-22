package com.xxx.user.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Created by wanghua on 17/3/24.
 */
public class ServiceConfigs {
    private static Configuration configs;
    public static String ygftest;
    public static String username0;
    public static String pws0;
    public static String username1;
    public static String pws1;
    public static String username2;
    public static String pws2;
    public static String username3;
    public static String pws3;



    private ServiceConfigs() {
    }

    static {
        try {
            configs = new PropertiesConfiguration("service.properties");
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        init();
    }

    private static void init() {
        ygftest = configs.getString("test.username");
        username0 = configs.getString("test.username0");
        pws0 = configs.getString("test.pws0");
        username1 = configs.getString("test.username1");
        pws1 = configs.getString("test.pws1");
        username2 = configs.getString("test.username2");
        pws2 = configs.getString("test.pws2");
        username3 = configs.getString("test.username3");
        pws3 = configs.getString("test.pws3");

    }
}
