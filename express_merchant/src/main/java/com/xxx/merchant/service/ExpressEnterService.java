package com.xxx.merchant.service;

import com.xxx.core.service.CommonService;
import com.xxx.model.business.ExpReceiveOrder;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:快件录入
 * @Author: disvenk.dai
 * @Date: 上午 10:36 2018/1/15 0015
 */
@Service
public class ExpressEnterService extends CommonService{

    /**
     * @Description:快件扫描录入
     * @Author: disvenk.dai
     * @Date: 上午 10:36 2018/1/15 0015
     */
    public ExpReceiveOrder expressEnterCheck(String deliveryNo,Integer schoolId){
        ExpReceiveOrder expReceiveOrder = get2(ExpReceiveOrder.class,"deliveryNo",deliveryNo,"schoolId",schoolId,"logicDeleted",false);
        return expReceiveOrder;
    }

    /**
     * @Description:快件查询
     * @Author: disvenk.dai
     * @Date: 上午 10:36 2018/1/15 0015
     */
    public List<ExpReceiveOrder> checkExpress(Integer pId,Integer schoolId,String no,String tel){
        Criterion cri = Restrictions.eq("logicDeleted",false);
        if(no!=null && StringUtils.isNotBlank(no)){
            cri = Restrictions.and(Restrictions.eq("deliveryNo",no));
        }
        if(tel!=null && StringUtils.isNotBlank(tel)){
            cri = Restrictions.and(Restrictions.eq("receiverTel",tel));
        }
    List<ExpReceiveOrder> expReceiveOrderList = getCurrentSession().createCriteria(ExpReceiveOrder.class)
        .add(cri)
        .add(Restrictions.eq("delivererId",pId))
        .add(Restrictions.eq("schoolId",schoolId))
        .list();
    return expReceiveOrderList;
    }

}
