package com.xxx.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by wanghua on 17/3/8.
 * [不推荐使用]请使用 commons-configuration.jar
 */
@Deprecated
public class PropertiesTools {
    private Properties properties = new Properties();

    public PropertiesTools(String filePath) {
        try {
            InputStream inputStream = PropertiesTools.class.getResourceAsStream(filePath);
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPropValue(String key) {
        return properties.get(key).toString();
    }
}
