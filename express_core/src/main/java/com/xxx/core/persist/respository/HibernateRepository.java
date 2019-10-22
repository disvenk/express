package com.xxx.core.persist.respository;

import com.alibaba.fastjson.JSON;
import com.xxx.core.entity.GenericEntity;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.security.MyUserDetails;
import com.xxx.core.security.SecurityUtils;
import com.xxx.core.query.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.*;
import org.hibernate.criterion.*;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created by wanghua on 17/1/19.
 */
public class HibernateRepository extends HibernateCacheManager implements Repository<Session> {

    public HibernateRepository() {
        this(false);
    }

    public HibernateRepository(boolean readonly) {
        super(readonly);
    }

    @Override
    public Object get(Class c, Serializable id) {
        return getCurrentSession().get(c, id);
    }

    @Override
    public Object get(Class c, String key, Object value) {
        return get(c, key, value, null, null, null, null);
    }

    @Override
    public Object get(Class c, String key1, Object value1, String key2, Object value2) {
        return get(c, key1, value1, key2, value2, null, null);
    }

    @Override
    public Object get(Class c, String key1, Object value1, String key2, Object value2, String key3, Object value3) {
        Criteria cri = getCurrentSession().createCriteria(c)
                .add(Restrictions.eq("logicDeleted", false))
                .add(Restrictions.eq(key1, value1));
        if (StringUtils.isNotBlank(key2) && value2 != null) {
            cri = cri.add(Restrictions.eq(key2, value2));
        }
        if (StringUtils.isNotBlank(key3) && value3 != null) {
            cri = cri.add(Restrictions.eq(key3, value3));
        }
        return cri.uniqueResult();
    }

    @Override
    public List getAll(Class c) {
        return getCurrentSession().createCriteria(c)
                .add(Restrictions.eq("logicDeleted", false)).setCacheable(false)
                .list();
    }

    @Override
    public PageList getList(Class entityClass, PageQuery pq) {
        //【分页参数设置】
        if (pq.rows != null && pq.limit == null) pq.limit = pq.rows;
        if (pq.page != null && pq.limit != null)
            pq.start = (pq.page - 1) * pq.limit;
        Integer start = pq.start == null ? pq.offset : pq.start;
        if (start == null)
            throw new RuntimeException("分页参数错误，请提供start或offset参数");
        if (pq.limit == null)
            throw new RuntimeException("分页参数错误，请提供limit参数");

        //【排序参数设置】（目前不支持关联实体字段的排序）
        if (StringUtils.isNotBlank(pq.order)) {  //1) bootstrap-table单字段格式排序：?sort=id&order=asc
            String sort = StringUtils.isBlank(pq.sort) ? "id" : pq.sort;
            if ("asc".equalsIgnoreCase(pq.order))
                pq.hibernateOrders.add(Order.asc(sort));
            else if ("desc".equalsIgnoreCase(pq.order))
                pq.hibernateOrders.add(Order.desc(sort));
        } else {  //2)extjs grid多字段格式排序：sort=[{"property":"Id","direction":"ASC"}]
            if (StringUtils.isNotBlank(pq.sort)) {
                List<ExtSort> extSorts = JSON.parseArray(pq.sort, ExtSort.class);
                for (ExtSort item : extSorts) {
                    if ("asc".equalsIgnoreCase(item.direction))
                        pq.hibernateOrders.add(Order.asc(item.property));
                    else if ("desc".equalsIgnoreCase(item.direction))
                        pq.hibernateOrders.add(Order.desc(item.property));
                }
            }
        }
        if (pq.hibernateOrders.size() == 0)  //如果没有排序信息，则使用默认排序
            pq.hibernateOrders.add(Order.desc("id"));

        //【过滤参数设置】
        AliasMappingWrapper aliasMappingWrapper = ExtFilter.parse(pq.filter, entityClass);
        Criterion criterion = Restrictions.eq("logicDeleted", pq.logicDeleted);
        if (pq.companyid != null)
            criterion = Restrictions.and(criterion, Restrictions.eq("companyId", pq.companyid));
        if (aliasMappingWrapper.criterion != null)
            criterion = Restrictions.and(criterion, aliasMappingWrapper.criterion);
        if (pq.hibernateCriterion != null)
            criterion = Restrictions.and(criterion, pq.hibernateCriterion);

        //ElementCollection elementCollection = AnnotationUtils.findAnnotation(entityClass, ElementCollection.class);
        //if (elementCollection == null)
        //    throw new RuntimeException("严重警告：当前实体中存在贴有 @ElementCollection 特性字段，但 Hibernate Criterion.list() 查询模式下，如果不进行关联过滤，使用此种内嵌实体存在严重问题（left join 后数据有可能多于主实体）。");

        //创建 Criteria：过滤并添加join连接
        //注意:getCurrentSession() 是基于当前线程的。即使你在其他线程中获得了这个baseServer Bean对象没有意义，应为他
        Criteria criteria = getCurrentSession().createCriteria(entityClass);
        Criteria criteria2 = getCurrentSession().createCriteria(entityClass);
        if (aliasMappingWrapper.criterion != null && aliasMappingWrapper.aliasMapping != null) {
            Iterator<Map.Entry<String, String>> iterator = aliasMappingWrapper.aliasMapping.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                criteria.createAlias(entry.getKey(), entry.getValue());  //会执行 left outer join。在一对多时，如果设置了饥渴加载也会进行左连接，所以使用时必须谨慎，如果出现重复的结果集，说明你的中间过滤条件并不唯一。
                criteria2.createAlias(entry.getKey(), entry.getValue());
            }
        }
        criteria.add(criterion);
        criteria2.add(criterion);

        //分页
        criteria.setFirstResult(start).setMaxResults(pq.limit);
        //排序
        for (Order item : pq.hibernateOrders)
            criteria.addOrder(item);
        //fetch(join)
        if (!StringUtils.isBlank(pq.hibernateFetchFields)) {
            for (String f : pq.hibernateFetchFields.split(",")) {
                criteria = criteria.setFetchMode(f, FetchMode.JOIN);
            }
        }
        List rows = criteria.setCacheable(pq.hibernateCacheable).list();
        pq.total = (long) criteria2.setProjection(Projections.rowCount()).uniqueResult(); //获取总数
        return new PageList(rows, pq.total);
    }

    protected long aggregate(Class c, Criterion criterion, Projection projection) {
        if (criterion == null)
            criterion = Restrictions.eq("logicDeleted", false);
        else
            criterion = Restrictions.and(criterion, Restrictions.eq("logicDeleted", false));

        Criteria criteria = getCurrentSession().createCriteria(c).add(criterion).setProjection(projection);
        return (long) criteria.uniqueResult();
    }

    public SQLQuery procedureSqlBuilder(String procedureName, String... params) {
        List<String> q = new ArrayList<>();
        for (int i = 0; i < params.length; i++)
            q.add("?");
        SQLQuery query = getCurrentSession().createSQLQuery("{call p_autoUpdateCompanyCK(" + StringUtils.join(q, ",") + ")}");
        for (int i = 0; i < params.length; i++)
            query.setString(i, params[i]);
        //query.executeUpdate();
        return query;
    }


    /**
     * 新增或修改
     * 提示：如果更新了外键，比如companyId，对应的company仍然是null的，需要开发人员根据情况调用 refresh() 下才会有。
     *
     * @param entity
     * @return
     * @throws UpsertException
     */
    @Override
    public Object upsert(GenericEntity entity) throws UpsertException {
        Integer createdBy = null;
        Integer createdChannel=null;
        MyUserDetails myUserDetails = SecurityUtils.getPrincipal();
        if (myUserDetails != null && myUserDetails.getUserId() != null) {
            createdBy = myUserDetails.getUserId();
            createdChannel = myUserDetails.getUserType();
        }
        if (entity.getId() == null) {
            entity.setCreatedDate(new Date());
            entity.setCreatedBy(createdBy);
            entity.setCreatedChannel(createdChannel);
        } else {
            entity.setUpdateBy(createdBy);
            entity.setUpdateChannel(createdChannel);
            entity.setUpdateDate(new Date());

            GenericEntity genericEntity = (GenericEntity) this.get(entity.getClass(), entity.getId());
            entity.setVersion(genericEntity.getVersion());
            entity.setAppId(genericEntity.getAppId());
            entity.setLogicDeleted(genericEntity.isLogicDeleted());
            entity.setCreatedBy(genericEntity.getCreatedBy());
            entity.setCreatedDate(genericEntity.getCreatedDate());
        }

        GenericEntity persistentEntity = (GenericEntity) getCurrentSession().merge(entity);  //getCurrentSession().flush();  //使用了事物不需要调用此方法的。

        flush();
        //hibernateCacheManager.refresh(persistentEntity);  //如果更新了外键，比如companyId，对应的company仍然是null的，需要调用此refresh下才会有

        //entity.setId(persistentEntity.getId());
        //entity.setVersion(persistentEntity.getVersion());
        return persistentEntity;
    }

    @Override
    public void delete(GenericEntity entity) {
        getCurrentSession().delete(entity);
    }

    @Override
    public void delete(Collection<? extends GenericEntity> entities) {
        Iterator<?> objectIterator = entities.iterator();
        while (objectIterator.hasNext()) {
            getCurrentSession().delete(objectIterator.next());
        }
    }

    /**
     * 批量删除
     * @param entityClass 待删除的实体类型
     * @param ids 待删除实体的ID集合
     * @return 受影响的行数
     */
    @Override
    public int delete(Class entityClass,Object[] ids) {

        String where = "";
        for (int i = 0; i < ids.length; i++) {
            if (i == 0)
                where = "id=" + ids[i];
            else
                where = where + " or id=" + ids[i];
        }
        String hql = MessageFormat.format("delete from {0} where {1}", entityClass.getName(), where);
        Query q = getCurrentSession().createQuery(hql);
        return q.executeUpdate();
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
