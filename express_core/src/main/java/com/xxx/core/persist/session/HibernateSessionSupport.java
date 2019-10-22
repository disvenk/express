package com.xxx.core.persist.session;

import com.xxx.core.persist.respository.HibernateUtils;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

/**
 * Created by wanghua on 17/1/19.
 */
public class HibernateSessionSupport extends HibernateDaoSupport implements SessionSupport<Session> {
    public static HibernateTemplate hibernateTemplate = HibernateUtils.hibernateTemplate;  //分装了部分 session 操作，springMvc中可直接使用它
    public static HibernateTemplate hibernateReadonlyTemplate = HibernateUtils.hibernateReadonlyTemplate;

    private Session session;  //适用于 web+其他线程，创建其他线程时请用 setManualSession()
    boolean isManual = false;

    @Override
    public void openThreadSession(boolean readonly) {
        if (readonly) {
            this.session = hibernateReadonlyTemplate.getSessionFactory().openSession();
        } else {
            //FlushMode.MANUAL——操作过程中hibernate会将事务设置为readonly，所以在增加、删除或修改操作过程中会出现如下错误：
            //          Write operations are not allowed in read-only mode (FlushMode.MANUAL): Turn your Session into
            //          FlushMode.COMMIT/AUTO or remove 'readOnly' marker from transaction definition.
            //FlushMode.AUTO——设置成auto之后，当程序进行查询、提交事务或者调用session.flush()的时候，都会使缓存和数据库进行同步，也就是刷新数据库
            //FlushMode.COMMIT——提交事务或者session.flush()时，刷新数据库；查询不刷新
            this.session = hibernateTemplate.getSessionFactory().openSession();
            this.session.setFlushMode(FlushMode.AUTO);
        }
        isManual = true;
    }

    @Override
    public Session getCurrentSession() {
        if (!isManual)
            session = currentSession();
        return session;
    }

    @Override
    public void close() {
        if (session != null) {
            session.close();
        }
        getSessionFactory().close();
    }
}
