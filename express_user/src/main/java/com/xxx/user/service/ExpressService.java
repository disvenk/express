package com.xxx.user.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.*;
import com.xxx.user.cache.VerifyCache;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpressService extends CommonService {

    /**
     * @Description: 获取快递公司列表
     * @Author: Chen.zm
     * @Date: 2018/1/8 0008
     */
    public List<ExpDeliveryCompany> findList() {
        Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
        return getCurrentSession().createCriteria(ExpDeliveryCompany.class)
                .add(cri)
                .addOrder(Order.desc("id"))
                .list();
    }

    /**
     * @Description: 取得所有省份
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    public List<ExpArea> findProvince() {
        Criterion cri = Restrictions.eq("type", 1);
        return getCurrentSession().createCriteria(ExpArea.class)
                .add(cri)
                .addOrder(Order.asc("id"))
                .list();
    }

    /**
     * @Description: 取得所有省市区联动
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    public List<ExpArea> findArea(String code) {
        Criterion cri = Restrictions.eq("parentCode", code);
        return getCurrentSession().createCriteria(ExpArea.class)
                .add(cri)
                .addOrder(Order.asc("id"))
                .list();
    }

    /**
     * @Description: 取得省市区
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    public JSONArray findProvinceCityZone() {
        List<ExpArea> list = getCurrentSession().createCriteria(ExpArea.class)
                .addOrder(Order.asc("id"))
                .list();
        Map<String, JSONObject> areaMap = new LinkedHashMap<>();
        Map<String, String> codeMap = new LinkedHashMap<>();
        for (ExpArea area : list) {
            if (area.getType() == 1) {
                JSONObject json = new JSONObject();
                json.put("id", area.getId());
                json.put("code", area.getCode());
                json.put("name", area.getName());
                json.put("sub", new JSONArray());
                areaMap.put(area.getCode(), json);
                continue;
            }
            if (area.getType() == 2) {
                JSONObject json = new JSONObject();
                json.put("id", area.getId());
                json.put("code", area.getCode());
                json.put("name", area.getName());
                json.put("sub", new JSONArray());
                ((JSONArray) areaMap.get(area.getParentCode()).get("sub")).add(json);
                codeMap.put(area.getCode(), area.getParentCode());
                continue;
            }
            if (area.getType() == 3) {
                JSONObject json = new JSONObject();
                json.put("id", area.getId());
                json.put("code", area.getCode());
                json.put("name", area.getName());
                for (Object city : (JSONArray) areaMap.get(codeMap.get(area.getParentCode())).get("sub")) {
                    if (((JSONObject) city).get("code").equals(area.getParentCode())) {
                        ((JSONArray) ((JSONObject) city).get("sub")).add(json);
                    }
                }
                continue;
            }
        }
        //封装数据
        JSONArray data = new JSONArray();
        for (JSONObject json : areaMap.values()) {
            data.add(json);
        }
        return data;
    }

    /**
     * @Description: 各个网点快递员上门取件时间列表
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    public List<ExpSchoolWorkTime> getDeliveryTimeFromStudent(Integer schoolId) {
        Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
        cri = Restrictions.and(cri, Restrictions.eq("type", 0));
        cri = Restrictions.and(cri, Restrictions.eq("schoolId", schoolId));
        return getCurrentSession().createCriteria(ExpSchoolWorkTime.class)
                .add(cri)
                .addOrder(Order.asc("id"))
                .list();
    }

    /**
     * @Description: 各个网点的派件上门时间
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    public List<ExpSchoolWorkTime> sendTimeFromDeliveryer(Integer schoolId) {
        Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
        cri = Restrictions.and(cri, Restrictions.eq("type", 1));
        cri = Restrictions.and(cri, Restrictions.eq("schoolId", schoolId));
        return getCurrentSession().createCriteria(ExpSchoolWorkTime.class)
                .add(cri)
                .addOrder(Order.asc("id"))
                .list();
    }

    /**
     * @Description: 根据网点查询快递公司
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    public List<ExpDeliveryCompany> findCompayBySchool(Integer schoolId){
        List<ExpSchoolDeliveryCompany> list = getCurrentSession().createCriteria(ExpSchoolDeliveryCompany.class)
                .add(Restrictions.eq("logicDeleted",false))
                .add(Restrictions.eq("schoolId",schoolId))
                .list();
        List<ExpDeliveryCompany> deliveryCompanyList = new ArrayList<>();
        for(ExpSchoolDeliveryCompany ExpSchoolDeliveryCompany : list){
            ExpDeliveryCompany expDeliveryCompany = get2(ExpDeliveryCompany.class,"id",ExpSchoolDeliveryCompany.getDeliveryId());
            deliveryCompanyList.add(expDeliveryCompany);
        }

        return deliveryCompanyList;
    }


}
