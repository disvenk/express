package com.xxx.admin.dao;


import com.alibaba.fastjson.JSONObject;
import com.xxx.core.dao.CommonDao;
import com.xxx.core.query.MybatisPageQuery;
import com.xxx.core.query.PageList;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SuplierDao extends CommonDao{

    public JSONObject findHomeInfo(Integer suplierId){
        Map<String, Object> map = new HashMap<>();
        map.put("suplierId", suplierId);
        return mybatisRepository.getCurrentSession().selectOne("mybatis.mappers.SuplierMapper.findHomeInfo",map);
    }

    public JSONObject orderCount(Integer suplierId, String startDate, String endDate){
        Map<String, Object> map = new HashMap<>();
        map.put("suplierId", suplierId);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        return mybatisRepository.getCurrentSession().selectOne("mybatis.mappers.SuplierMapper.orderCount",map);
    }

    public List<JSONObject> producedStatusCount(Integer suplierId, Integer orderType){
        Map<String, Object> map = new HashMap<>();
        map.put("suplierId", suplierId);
        map.put("orderType", orderType);
        return mybatisRepository.getCurrentSession().selectList("mybatis.mappers.SuplierMapper.producedStatusCount",map);
    }

    public List<JSONObject> findSendOrderCount(JSONObject pageQuery){
        List<JSONObject> list = mybatisReadonlyRepository.getCurrentSession()
                .selectList("mybatis.mappers.SuplierMapper.findSendOrderCount",pageQuery);
        return   list;
    }

    public List<JSONObject> findSendQtyCount(JSONObject pageQuery){
        List<JSONObject> list = mybatisReadonlyRepository.getCurrentSession()
                .selectList("mybatis.mappers.SuplierMapper.findSendQtyCount", pageQuery);
        return  list;
    }

    public List<JSONObject> findReceiveOrderCount(JSONObject pageQuery){
        return mybatisRepository.getCurrentSession().selectList("mybatis.mappers.SuplierMapper.findReceiveOrderCount",pageQuery);
    }

    public List<JSONObject> findReceiveQtyCount(JSONObject pageQuery){
        return mybatisRepository.getCurrentSession().selectList("mybatis.mappers.SuplierMapper.findReceiveQtyCount",pageQuery);
    }

    public JSONObject findDelivererCount(Integer delivererId,Integer schoolId){
        Map<String, Object> map = new HashMap<>();
        map.put("delivererId", delivererId);
        map.put("schoolId", schoolId);
        return mybatisRepository.getCurrentSession().selectOne("mybatis.mappers.SuplierMapper.findDelivererCount",map);
    }

    public PageList<JSONObject> findList(MybatisPageQuery pageQuery) {
        List<JSONObject> list = mybatisReadonlyRepository.getCurrentSession()
                .selectList("mybatis.mappers.SuplierMapper.findList", pageQuery.getParams(), pageQuery);
        return  new PageList(list, pageQuery.getTotal());
    }

}
