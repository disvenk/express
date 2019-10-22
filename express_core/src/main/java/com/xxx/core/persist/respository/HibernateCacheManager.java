package com.xxx.core.persist.respository;

import com.xxx.core.persist.session.HibernateSessionSupport;
import com.xxx.core.spring.SpringContext;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.SessionFactory;

import java.io.Serializable;

/**
 * Created by wanghua on 17/1/20.
 */
public class HibernateCacheManager extends HibernateSessionSupport {

    public HibernateCacheManager(boolean readonly) {
        try {
            if (readonly) {
                SessionFactory sessionFactory = (SessionFactory) SpringContext.getApplicationContext().getBean("sessionReadonlyFactory");
                super.setSessionFactory(sessionFactory);
            } else {
                //SessionFactory sessionFactory = SpringContext.getApplicationContext().getBean(SessionFactory.class);
                SessionFactory sessionFactory = (SessionFactory) SpringContext.getApplicationContext().getBean("sessionFactory");
                super.setSessionFactory(sessionFactory);
            }
        } catch (Exception ex) {
            throw new RuntimeException("HibernateCacheManager 初始化失败！");
        }
    }

    /**
     * 一级缓存的管理：将指定的持久化对象从一级缓存中清除,释放对象所占用的内存资源,指定对象从持久化状态变为脱管状态,从而成为游离对象.
     *
     * @param entity
     */
    public void evict(GenericEntity entity) {
        getCurrentSession().evict(entity);
    }

    /**
     * 一级缓存的管理：判断指定的对象是否存在于一级缓存中.
     *
     * @param entity
     * @return
     */
    public boolean contains(GenericEntity entity) {
        return getCurrentSession().contains(entity);
    }

    /**
     * 一级缓存的管理：刷新一级缓存区的内容,使之与数据库数据保持同步。在同一个事物操作中，当用hibernate方法update或insert数据后，如果立刻使用mybatis获取其中数据是脏数据了。update或insert数据后调用此flush可以解决这个问题。
     */
    public void flush() {
        getCurrentSession().flush();
    }

    /**
     * 一级缓存的管理：刷新一级缓存区的指定的对象，使之与数据库数据保持同步
     *
     * @param entity
     */
    public void refresh(GenericEntity entity) {
        getCurrentSession().refresh(entity);
    }

    /**
     * 一级缓存的管理：将一级缓存中的所有持久化对象清除,释放其占用的内存资源
     */
    public void clear() {
        getCurrentSession().clear();
    }

    /**
     * 二级缓存的管理：将某个类的指定ID的持久化对象从二级缓存中清除,释放对象所占用的资源.
     *
     * @param cls
     * @param id
     */
    public void evictEntity(Class cls, Serializable id) {
        getSessionFactory().getCache().evictEntity(cls, id);
    }

    /**
     * 二级缓存的管理：将指定类的所有持久化对象从二级缓存中清除,释放其占用的内存资源
     *
     * @param cls
     */
    public void evictEntityRegion(Class cls) {
        getSessionFactory().getCache().evictEntityRegion(cls);
    }

    /**
     * 二级缓存的管理：将指定类的所有持久化对象的指定集合从二级缓存中清除,释放其占用的内存资源.
     *
     * @param var1
     */
    public void evictCollectionRegion(String var1) {   //var1 格式如：Customer.orders
        getSessionFactory().getCache().evictCollectionRegion(var1);
    }

}
