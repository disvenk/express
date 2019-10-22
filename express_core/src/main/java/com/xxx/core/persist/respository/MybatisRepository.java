package com.xxx.core.persist.respository;

import com.xxx.core.entity.GenericEntity;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import org.apache.ibatis.session.SqlSession;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Created by wanghua on 17/1/19.
 */
public class MybatisRepository extends MybatisCacheManager implements Repository<SqlSession> {
    public MybatisRepository() {
        this(false);
    }

    public MybatisRepository(boolean readonly) {
        super(readonly);
    }

    @Override
    public Object get(Class c, Serializable id) {
        return null;
    }

    @Override
    public Object get(Class c, String key, Object value) {
        return null;
    }

    @Override
    public Object get(Class c, String key1, Object value1, String key2, Object value2) {
        return null;
    }

    @Override
    public Object get(Class c, String key1, Object value1, String key2, Object value2, String key3, Object value3) {
        return null;
    }

    @Override
    public List getAll(Class c) {
        return null;
    }

    @Override
    public PageList getList(Class entityClass, PageQuery pq) throws Exception {
        return null;
    }

    @Override
    public Object upsert(GenericEntity entity) throws UpsertException {
        return null;
    }

    @Override
    public void delete(GenericEntity entity) {

    }

    @Override
    public void delete(Collection<? extends GenericEntity> entities) {

    }

    @Override
    public int delete(Class entityClass, Object[] ids) {
        return 0;
    }

    @Override
    public <TG extends GenericEntity> TG get2(Class c, Serializable id) {
        return (TG) get(c, id);
    }

    @Override
    public <TG extends GenericEntity> TG get2(Class c, String key, Object value) {
        return (TG) get(c, key, value);
    }

    @Override
    public <TG extends GenericEntity> TG get2(Class c, String key1, Object value1, String key2, Object value2) {
        return (TG) get(c, key1, value1, key2, value2);
    }

    @Override
    public <TG extends GenericEntity> TG get2(Class c, String key1, Object value1, String key2, Object value2, String key3, Object value3) {
        return (TG) get(c, key1, value1, key2, value2,key3,value3);
    }


    @Override
    public <TG extends GenericEntity> TG upsert2(TG obj) throws UpsertException {
        return (TG) upsert(obj);
    }
}

