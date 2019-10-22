package com.xxx.merchant.service;

import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.ExpReceiveOrder;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.service.UploadFileService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: disvenk.dai
 * @Date: 下午 4:24 2018/1/15 0015
 */
@Service
public class SelfTakeExpressService extends CommonService{
    @Autowired
    private UploadFileService uploadFileService;

    /**
     * @Description: 自行取件列表
     * @Author: disvenk
     * @Date: 2018/1/15
     */
 /*   @Cacheable(value = {"ExpReceiveOrder"})
    public List<ExpReceiveOrder> selfTakeList(){
        List<ExpReceiveOrder> list = getCurrentSession().createCriteria(ExpReceiveOrder.class)
               // .add(Restrictions.eq("delivererId", CurrentUser.get().userId))
                .add(Restrictions.eq("schoolId", CurrentUser.get().schoolId))
                .add(Restrictions.eq("orderStatus", 2))
                //.add(Restrictions.eq("type", 0))
                .add(Restrictions.eq("logicDeleted",false))
                .addOrder(Order.desc("updateDate"))
                .list();
        return list;
    }*/

    /**
     * @Description: 快件签收确认签收详情
     * @Author: disvenk
     * @Date: 2018/1/15
     */
    /*public ExpReceiveOrder selfTakeSingle(Integer id) throws ResponseEntityException {
        ExpReceiveOrder expReceiveOrder = get2(ExpReceiveOrder.class,"id",id,"logicDeleted",false);
        if(expReceiveOrder==null){
            throw new ResponseEntityException(220,"该订单不存在");
        }
        return expReceiveOrder;
    }*/

    /**
     * @Description: 快件签收确认签收详情
     * @Author: disvenk
     * @Date: 2018/1/16
     */
    public ExpReceiveOrder selfTakeSign(String deliveryNo) throws ResponseEntityException {
        //ExpReceiveOrder expReceiveOrder = get2(ExpReceiveOrder.class,"deliveryNo",deliveryNo,"logicDeleted",false);
        ExpReceiveOrder expReceiveOrder = (ExpReceiveOrder) getCurrentSession().createCriteria(ExpReceiveOrder.class)
                .add(Restrictions.eq("logicDeleted",false))
                .add(Restrictions.eq("deliveryNo",deliveryNo))
                .add(Restrictions.eq("orderStatus",2))
                .uniqueResult();

        if(expReceiveOrder==null){
            throw new ResponseEntityException(220,"该订单不存在");
        }
        return expReceiveOrder;
    }

    /**
     * @Description: 快件签收模块签收
     * @Author: disvenk
     * @Date: 2018/1/15
     */
    public ExpReceiveOrder sureSign(Integer id,String base64,Integer delivererId,Integer schoolId) throws ResponseEntityException, UpsertException {
      ExpReceiveOrder expReceiveOrder = (ExpReceiveOrder) getCurrentSession().createCriteria(ExpReceiveOrder.class)
                .add(Restrictions.eq("logicDeleted",false))
                .add(Restrictions.eq("id",id))
                //.add(Restrictions.eq("delivererId",delivererId))
                .uniqueResult();
      if(expReceiveOrder==null){
          throw new ResponseEntityException(220,"该订单不存在");
      }
      if(StringUtils.isNotBlank(base64)){
          Integer uploadId = uploadFileService.saveOssUploadFileByBase64(base64);
          expReceiveOrder.setCapture(uploadId.toString());
      }
      expReceiveOrder.setOrderStatus(6);
      expReceiveOrder.setExpressDate(new Date());
      return upsert2(expReceiveOrder);
    }
}
