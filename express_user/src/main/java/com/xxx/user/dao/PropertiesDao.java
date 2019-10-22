package com.xxx.user.dao;

import com.alibaba.fastjson.JSONObject;
import com.xxx.core.dao.CommonDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;


@Repository("PropertiesDao")
public class PropertiesDao extends CommonDao {

    /**
     * 获取寄件订单号
     */
    public String generateBoxPayOrderNumber() {
        Map map = new HashMap<String, Long>();
        map.put("result", null);
        mybatisRepository.getCurrentSession().selectOne("mybatis.mappers.PropertiesMapper.generateSendOrderNumber", map);
        return  "" + map.get("result");
    }
}
