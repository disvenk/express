package com.xxx.merchant.service;

import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.merchant.form.IdForm;
import com.xxx.model.business.ExpReceiveOrder;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.service.UploadFileService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: disvenk.dai
 * @Date: 下午 1:57 2018/1/15 0015
 */
@Service
public class UnTakeOrderService extends CommonService{

    @Autowired
    private UploadFileService uploadFileService;

    /**
     * @Description: 待接单列表
     * @Author: disvenk
     * @Date: 2018/1/15
     */
    @Cacheable(value = "{ExpReceiveOrder}")
    public PageList<ExpReceiveOrder> orderTakeList(PageQuery pageQuery, IdForm form){
        Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted",false)
        ,Restrictions.eq("schoolId",CurrentUser.get().schoolId));
        if(form.orderStatus==3){
          cri = Restrictions.and(cri, Restrictions.eq("orderStatus",3));
        }else if(form.orderStatus==4){
            cri = Restrictions.and(cri,Restrictions.in("orderStatus",4,5,6)
                    ,Restrictions.eq("delivererId",CurrentUser.get().userId));
        }

        pageQuery.hibernateCriterion=cri;
        pageQuery.order="desc";
        pageQuery.sort="updateDate";
        PageList<ExpReceiveOrder> list = hibernateReadonlyRepository.getList(ExpReceiveOrder.class,pageQuery);
        return list;
    }

    /**
     * @Description: 待接单详情
     * @Author: disvenk
     * @Date: 2018/1/15
     */
    public ExpReceiveOrder orderTakingDetail(Integer id) throws ResponseEntityException {
        ExpReceiveOrder expReceiveOrder = (ExpReceiveOrder) getCurrentSession().createCriteria(ExpReceiveOrder.class)
                .add(Restrictions.eq("id", id))
                .add(Restrictions.eq("logicDeleted",false))
                .addOrder(Order.desc("updateDate"))
                .uniqueResult();
        if(expReceiveOrder==null){
            throw new ResponseEntityException(220,"该订单不存在");
        }
        return expReceiveOrder;
    }

    /**
     * @Description: 确认接单
     * @Author: disvenk
     * @Date: 2018/1/16
     */
    @CacheEvict(value = "{ExpReceiveOrder}",allEntries = true)
    public ExpReceiveOrder sureTaked(Integer schoolId,Integer id) throws ResponseEntityException, UpsertException {
      ExpReceiveOrder expReceiveOrder = (ExpReceiveOrder) getCurrentSession().createCriteria(ExpReceiveOrder.class)
                .add(Restrictions.eq("logicDeleted",false))
                .add(Restrictions.eq("id",id))
                .uniqueResult();
      if(expReceiveOrder==null){
          throw new ResponseEntityException(220,"该订单不存在");
      }
      if(expReceiveOrder.getOrderStatus()!=3){
          throw  new ResponseEntityException(220,"当前订单状态不能接单");
      }
      if(expReceiveOrder.getPayStatus()==0){
          throw new ResponseEntityException(220,"该订单未支付，不能接单");
      }
      if(expReceiveOrder.getOrderStatus()==4){
        throw new ResponseEntityException(220,"手慢啦！该订单已被领取");
      }
      expReceiveOrder.setDelivererId(CurrentUser.get().userId);
      expReceiveOrder.setOrderStatus(4);
       return  upsert2(expReceiveOrder);
    }

    /**
     * @Description: 确认签收
     * @Author: disvenk
     * @Date: 2018/1/16
     */
    public ExpReceiveOrder sureSign(Integer id,String base64) throws UpsertException, ResponseEntityException {
       ExpReceiveOrder expReceiveOrder = get2(ExpReceiveOrder.class,"id",id);

//        Integer iconId = uploadFileService.saveOssUploadFileByBase64(base64);
        if(expReceiveOrder!=null && expReceiveOrder.getPayStatus()==1){
            if(StringUtils.isNotBlank(base64)){
                Integer uploadId = uploadFileService.saveOssUploadFileByBase64(base64);
                expReceiveOrder.setCapture(uploadId.toString());
            }
//            expReceiveOrder.setCapture(iconId.toString());
        expReceiveOrder.setOrderStatus(6);
        expReceiveOrder.setExpressDate(new Date());
        }
        return upsert2(expReceiveOrder);
    }
}
