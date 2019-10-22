package com.xxx.core.persist.respository;

import com.xxx.core.entity.GenericEntity;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageQuery;
import com.xxx.core.persist.session.SessionSupport;
import com.xxx.core.query.PageList;

import java.io.Closeable;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Created by wanghua on 17/1/18.
 * 通用查询
 */
public interface Repository<T extends Closeable> extends SessionSupport<T> {
    Object get(Class c, Serializable id);

    Object get(Class c, String key, Object value);

    Object get(Class c, String key1, Object value1, String key2, Object value2);

    Object get(Class c, String key1, Object value1, String key2, Object value2, String key3, Object value3);

    List getAll(Class c);

    PageList getList(Class entityClass, PageQuery pq) throws Exception;

    Object upsert(GenericEntity entity) throws UpsertException;

    void delete(GenericEntity entity);

    void delete(Collection<? extends GenericEntity> entities);

    int delete(Class entityClass, Object[] ids);

    //===== 下面是泛型版本 =====
    <TG extends GenericEntity> TG get2(Class c, Serializable id);

    public <TG extends GenericEntity> TG get2(Class c, String key, Object value);

    public <TG extends GenericEntity> TG get2(Class c, String key1, Object value1, String key2, Object value2);

    public <TG extends GenericEntity> TG get2(Class c, String key1, Object value1, String key2, Object value2, String key3, Object value3);

    public <TG extends GenericEntity> TG upsert2(TG obj) throws UpsertException;
}
