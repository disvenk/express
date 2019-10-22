package com.xxx.clients.dao;


import com.alibaba.fastjson.JSONObject;
import com.xxx.core.dao.CommonDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserHomeDao extends CommonDao{

    public JSONObject findHomeInfo(Integer userId){
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        return mybatisRepository.getCurrentSession().selectOne("mybatis.mappers.UserHomeMapper.findHomeInfo",map);
    }



}
