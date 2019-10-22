package com.xxx.core.persist.respository;

import com.xxx.core.spring.SpringContext;
import com.xxx.core.persist.session.MybatisSessionSupport;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

/**
 * Created by wanghua on 17/1/20.
 */
public class MybatisCacheManager extends MybatisSessionSupport {

    public MybatisCacheManager(boolean readonly) {
        try {
            if (readonly) {
                SqlSessionFactory sqlSessionFactory = ((DefaultSqlSessionFactory) SpringContext.getApplicationContext().getBean("sqlSessionReadonlyFactory"));
                super.setSqlSessionFactory(sqlSessionFactory);
            } else {
                //SqlSessionFactory sqlSessionFactory = SpringContext.getApplicationContext().getBean(SqlSessionFactoryBean.class).getObject();
                SqlSessionFactory sqlSessionFactory = ((DefaultSqlSessionFactory) SpringContext.getApplicationContext().getBean("sqlSessionFactory"));
                super.setSqlSessionFactory(sqlSessionFactory);
            }
        } catch (Exception ex) {
            throw new RuntimeException("MybatisCacheManager 初始化失败！");
        }
    }

    /**
     * 清理缓存
     */
    public void clearCache() {
        getCurrentSession().clearCache();
    }
}
