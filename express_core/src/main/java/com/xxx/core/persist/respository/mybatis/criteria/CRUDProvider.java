package com.xxx.core.persist.respository.mybatis.criteria;

//import com.SpringUtil;
//import com.cache.CacheNameConstant;
//import com.cache.MemcachedCacheManager;
//import com.cache.RedisCacheManager;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.Assert;

import java.util.Map;

public class CRUDProvider {

	public String insert(Map<String, Object> map) {
        Assert.notNull(map.get("entity"), "参数不能为空");
        EntityScan.ClassMapper classMapper = EntityScan.getClassMappers().get(map.get("entity").getClass());
        Assert.notNull(classMapper, "Entity不存在");
        String[] includeProperty = null;
        if(map.containsKey("includeProperty")){
            includeProperty = (String[])map.get("includeProperty");
        }
        String[] excludeProperty = null;
        if(map.containsKey("excludeProperty")){
            excludeProperty = (String[])map.get("excludeProperty");
        }

        //TODO 缓存框架搭建
        //cacheClear(map);
		return classMapper.insertSql(includeProperty, excludeProperty, "entity.");
	}

    public String update(Map<String, Object> map) {
        Assert.notNull(map.get("entity"), "参数不能为空");
        EntityScan.ClassMapper classMapper = EntityScan.getClassMappers().get(map.get("entity").getClass());
        Assert.notNull(classMapper, "Entity不存在");
        WhereCondition where = null;
        if (map.containsKey("where")) {
            where = (WhereCondition) map.get("where");
        }
        String[] includeProperty = null;
        if (map.containsKey("includeProperty")) {
            includeProperty = (String[]) map.get("includeProperty");
        }

        //cacheClear(map);
        return classMapper.updateSql(where, includeProperty, "entity.");
    }

    public String delete(Map<String, Object> map){
        Assert.notNull(map.get("entity"), "参数不能为空");
        EntityScan.ClassMapper classMapper = EntityScan.getClassMappers().get(map.get("entity").getClass());
        Assert.notNull(classMapper, "Entity不存在");
        WhereCondition where = null;
        if(map.containsKey("where")){
            where = (WhereCondition)map.get("where");
        }

        //cacheClear(map);
        return classMapper.deleteSql(where, "entity.");
    }

    public String get(Map<String, Object> map){
        Assert.notNull(map.get("class"), "类型不能为空");
        EntityScan.ClassMapper classMapper = EntityScan.getClassMappers().get(map.get("class"));
        Assert.notNull(classMapper, "Entity不存在");
        boolean cascade = false;
        if(map.containsKey("cascade")){
            boolean[] cc = ((boolean[])map.get("cascade"));
            if(ArrayUtils.isNotEmpty(cc)){
                cascade = cc[0];
            }
        }
        String[] includeProperty = null;
        if(map.containsKey("includeProperty")){
            includeProperty = (String[])map.get("includeProperty");
        }
        WhereCondition where = null;
        if(map.containsKey("where")){
            where = (WhereCondition)map.get("where");
            return classMapper.selectSql(where, includeProperty, null, null, 0, 1, "entity.", cascade, map.get("entity"));
        }else{
            //where = new String[]{classMapper.getIdName()};
            where = WhereBuilder.builder().and(classMapper.getIdName()).build();
            return classMapper.selectSql(where, null, null, null, 0, 1, "", cascade, new Object());
        }
    }

    public String list(Map<String, Object> map){
        Assert.notNull(map.get("class"), "类型不能为空");
        EntityScan.ClassMapper classMapper = EntityScan.getClassMappers().get(map.get("class"));
        Assert.notNull(classMapper, "Entity不存在");
        WhereCondition where = null;
        if(map.containsKey("where")){
            where = (WhereCondition)map.get("where");
        }
        String[] includeProperty = null;
        if(map.containsKey("includeProperty")){
            includeProperty = (String[])map.get("includeProperty");
        }
        String[] orderBy = null;
        if(map.containsKey("orderBy")){
            orderBy = (String[])map.get("orderBy");
        }
        Integer first = null;
        if(map.containsKey("first")){
            first = (Integer)map.get("first");
        }
        Integer max = null;
        if(map.containsKey("max")){
            max = (Integer)map.get("max");
        }
        boolean cascade = false;
        if(map.containsKey("cascade")){
            boolean[] cc = ((boolean[])map.get("cascade"));
            if(ArrayUtils.isNotEmpty(cc)){
                cascade = cc[0];
            }
        }
        return classMapper.selectSql(where, includeProperty, null, orderBy, first, max, "entity.", cascade, map.get("entity"));
    }

    public String count(Map<String, Object> map){
        Assert.notNull(map.get("class"), "类型不能为空");
        EntityScan.ClassMapper classMapper = EntityScan.getClassMappers().get(map.get("class"));
        Assert.notNull(classMapper, "Entity不存在");
        WhereCondition where = null;
        if(map.containsKey("where")){
            where = (WhereCondition)map.get("where");
        }
        boolean cascade = true;
        if(map.containsKey("cascade")){
            boolean[] cc = ((boolean[])map.get("cascade"));
            if(ArrayUtils.isNotEmpty(cc)){
                cascade = cc[0];
            }
        }
        return classMapper.selectCountSql(where, "entity.", cascade, map.get("entity"));
    }


//    private void cacheClear(Map<String, Object> map){
//        if(map.containsKey("cacheNames")) {
//            Object cacheNames = map.get("cacheNames");
//            if (cacheNames != null) {
//                //Redis
//                RedisCacheManager cacheManager = SpringUtil.getApplicationContext().getBean(RedisCacheManager.class);
//                String[] names = (String[]) cacheNames;
//                for (String name : names)
//                    cacheManager.getCache(name).clear();
//
//                /*
//                //Memcache
//                MemcachedCacheManager cacheManager = SpringUtil.getApplicationContext().getBean(MemcachedCacheManager.class);
//                String[] names = (String[]) cacheNames;
//                for (String name : names)
//                    cacheManager.getCache(name).clear();
//                */
//            }
//        }
//    }
}
