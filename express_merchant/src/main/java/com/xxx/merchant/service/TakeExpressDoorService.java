package com.xxx.merchant.service;

import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.ExpReceiveOrder;
import com.xxx.user.service.UploadFileService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Description:派件上门
 * @Author: disvenk.dai
 * @Date: 下午 4:20 2018/1/16 0016
 */
@Service
public class TakeExpressDoorService extends CommonService{
    @Autowired
    private UploadFileService uploadFileService;

    /**
     * @Description:
     * @Author: disvenk.dai
     * @Date: 下午 4:20 2018/1/16 0016
     */
    @Cacheable(value = {"ExpReceiveOrder"})
    public List<ExpReceiveOrder> takeToDoorList(Integer id,Integer schoolId){
       List<ExpReceiveOrder> expReceiveOrderList = getCurrentSession().createCriteria(ExpReceiveOrder.class)
        .add(Restrictions.eq("logicDeleted",false))
        .add(Restrictions.eq("delivererId",id))
        .add(Restrictions.eq("schoolId",schoolId))
        .add(Restrictions.eq("type",1))
        .list();
       return expReceiveOrderList;
    }

    /**
     * @Description: 上门派件确认签收详情
     * @Author: disvenk
     * @Date: 2018/1/16
     */
    public ExpReceiveOrder selfTakeSingle(Integer id) throws ResponseEntityException {
        ExpReceiveOrder expReceiveOrder = get2(ExpReceiveOrder.class,"id",id,"logicDeleted",false);
        if(expReceiveOrder==null){
            throw new ResponseEntityException(220,"该订单不存在");
        }
        return expReceiveOrder;
    }

    /**
     * @Description: 上门派件签收
     * @Author: disvenk
     * @Date: 2018/1/15
     */
    public ExpReceiveOrder sureSign(Integer id,String cabture,Integer delivererId,Integer schoolId) throws ResponseEntityException, UpsertException {
        ExpReceiveOrder expReceiveOrder = (ExpReceiveOrder) getCurrentSession().createCriteria(ExpReceiveOrder.class)
                .add(Restrictions.eq("logicDeleted",false))
                .add(Restrictions.eq("id",id))
                .add(Restrictions.eq("delivererId",delivererId))
                .add(Restrictions.eq("schoolId",schoolId))
                .uniqueResult();
        if(expReceiveOrder==null){
            throw new ResponseEntityException(220,"该订单不存在");
        }
        if(StringUtils.isNotBlank(cabture)){
            Integer uploadId = uploadFileService.saveOssUploadFileByBase64(cabture);
            expReceiveOrder.setCapture(uploadId.toString());
        }
        expReceiveOrder.setOrderStatus(6);
        expReceiveOrder.setExpressDate(new Date());
        return upsert2(expReceiveOrder);
    }
}
