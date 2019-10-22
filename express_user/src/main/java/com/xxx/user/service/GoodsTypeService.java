package com.xxx.user.service;

import com.xxx.core.service.CommonService;
import com.xxx.model.business.ExpGoodsType;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsTypeService extends CommonService {

/**
 * @Description: 物品类别列表
 * @Author: disvenk.dai
 * @Date: 2018/1/8
 */
    public List<ExpGoodsType> findExpGoodsTypeList() {
        Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
        return getCurrentSession().createCriteria(ExpGoodsType.class)
                .add(cri)
                .addOrder(Order.asc("id"))
                .list();
    }
}
