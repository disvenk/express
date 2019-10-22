package com.xxx.core.persist.respository;

import com.xxx.core.spring.SpringContext;
import org.mybatis.spring.SqlSessionTemplate;

public class MybatisUtils {
    public static SqlSessionTemplate sqlSessionTemplate;
    public static SqlSessionTemplate sqlSessionReadOnlyTemplate;

    static {
        try {
            sqlSessionTemplate = (SqlSessionTemplate) SpringContext.getApplicationContext().getBean("sqlSessionTemplate");  //单例，线程安全的
        } catch (Throwable e) {
            throw new RuntimeException("无法获取Bean：sqlSessionTemplate");
        }

        try {
            sqlSessionReadOnlyTemplate = (SqlSessionTemplate) SpringContext.getApplicationContext().getBean("sqlSessionReadOnlyTemplate");  //单例，线程安全的
        } catch (Throwable e) {
            throw new RuntimeException("无法获取Bean：sqlSessionReadOnlyTemplate");
        }
    }
}
