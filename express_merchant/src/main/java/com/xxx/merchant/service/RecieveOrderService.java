package com.xxx.merchant.service;

import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.merchant.form.ExpressQueryForm;
import com.xxx.merchant.form.IdForm;
import com.xxx.merchant.form.MyReceiveOrderForm;
import com.xxx.model.business.*;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.service.UploadFileService;
import com.xxx.user.utils.GenerateNumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service("RecieveOrderService2")
public class RecieveOrderService extends CommonService{

    @Autowired
    private UploadFileService uploadFileService;

    /**
     * @Description: 平台端：录入不存在时，保存至我的订单
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @CacheEvict(value = "ExpReceiveOrder",allEntries = true)
    public ExpReceiveOrder enterUpdate(MyReceiveOrderForm form, Integer schoolId) throws ResponseEntityException, UpsertException {
        ExpDeliveryCompany expDeliveryCompany = get2(ExpDeliveryCompany.class,"name",form.deliveryCompanyName);
        //ExpGoodsType expGoodsType = get2(ExpGoodsType.class,"name",form.typeName);
        if(form.id!=null){
        ExpReceiveOrder expReceiveOrder = get2(ExpReceiveOrder.class, "id", form.id, "logicDeleted", false);
        expReceiveOrder.setValidateCode(expDeliveryCompany.getShortName()+form.validateCode);
			expReceiveOrder.setOrderStatus(2);
            expReceiveOrder.setArrivedDate(new Date());
            return upsert2(expReceiveOrder);
        } else {
            ExpReceiveOrder  expReceiveOrder = get2(ExpReceiveOrder.class,"deliveryNo",form.deliveryNo);
            if(expReceiveOrder!=null){
                throw new ResponseEntityException(220,"该订单号已存在系统");
            }
            ExpReceiveOrder order = new ExpReceiveOrder();
            ExpUser expUser = get2(ExpUser.class,"userMobile",form.tel,"schoolId",CurrentUser.get().schoolId,"loginType",1);
            if(expUser!=null){
                //throw new ResponseEntityException(220,"该手机号没有注册到网点");
                order.setUserId(expUser.getId());
                //order.setReceiverTel(expUser==null?"":expUser.getUserMobile());
            }else {
                order.setReceiverTel(form.tel);
                order.setReceiverName(expUser==null?"":expUser.getRelName());
            }
            order.setSchoolId(schoolId);
            order.setOrderNo(GenerateNumberUtil.generateSendExpressNumber());
            order.setOrderStatus(2);
            order.setType(0);
            order.setDeliveryId(expDeliveryCompany.getId());
            order.setDeliveryNo(form.deliveryNo);
           // order.setGoodsTypeId(expGoodsType.getId());
            order.setGoodsTypeName(form.typeName);
            order.setGoodsDescription(form.desc);
            order.setCreatedDate(new Date());
            order.setArrivedDate(new Date());
            order.setValidateCode(form.validateCode);
            return upsert2(order);
        }
    }

    /**
     * @Description: 自行取件订单
     * @Author: ,disvenk.dai
     * @Date: 2018/1/13
     */
    @Cacheable(value = "ExpReceiveOrder",key="#pageQuery.page"+"_"+"#pageQuery.pageSize")
    public PageList<ExpReceiveOrder> findSelfTake(PageQuery pageQuery, ExpressQueryForm form){

        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(form.deliveryNo) && form.deliveryNo != null)
            cri = Restrictions.and(cri, Restrictions.like("deliveryNo", form.deliveryNo, MatchMode.ANYWHERE));
         cri = Restrictions.and(cri,
                Restrictions.eq("type",form.orderType),
                Restrictions.eq("schoolId",CurrentUser.get().schoolId),
                Restrictions.eq("orderStatus",form.orderStatus));

        pageQuery.hibernateCriterion=cri;
        pageQuery.order="desc";
        pageQuery.sort="arrivedDate";
        PageList<ExpReceiveOrder> list = hibernateReadonlyRepository.getList(ExpReceiveOrder.class, pageQuery);
        return  list;
    }

    /**
     * @Description: 自行取件详情
     * @Author: ,disvenk.dai
     * @Date: 2018/1/13
     */
    public ExpReceiveOrder selfTakeDetail(Integer id) throws ResponseEntityException {
       ExpReceiveOrder expReceiveOrder = get2(ExpReceiveOrder.class,"id",id);
       if(expReceiveOrder==null){
           throw new ResponseEntityException(220,"该快递单不存在");
       }
    return  expReceiveOrder;
    }

    /**
     * @Description: 录入查询
     * @Author: ,disvenk.dai
     * @Date: 2018/1/13
     */
    public ExpReceiveOrder enterCheck(String deliveryNo,Integer schoolId){
        return  get2(ExpReceiveOrder.class,"deliveryNo",deliveryNo,"schoolId",schoolId);
    }

    /**
     * @Description: 确认签收
     * @Author: ,disvenk.dai
     * @Date: 2018/1/13
     */
    public ExpReceiveOrder sureSign(Integer id,String base64) throws ResponseEntityException, UpsertException {
    ExpReceiveOrder expReceiveOrder = get2(ExpReceiveOrder.class,id);
        if(StringUtils.isNotBlank(base64)){
            Integer uploadId = uploadFileService.saveOssUploadFileByBase64(base64);
            expReceiveOrder.setCapture(uploadId.toString());
        }
    expReceiveOrder.setOrderStatus(6);
    expReceiveOrder.setExpressDate(new Date());
    return  upsert2(expReceiveOrder);
    }

    /**
     * @Description: 拒收订单列表
     * @Author: ,disvenk.dai
     * @Date: 2018/1/13
     */
    @Cacheable(value = {"ExpReceiveOrder"})
    public PageList<ExpReceiveOrder> findRefuseList(PageQuery pageQuery,IdForm form){
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(form.deliveryNo) && form.deliveryNo != null)
            cri = Restrictions.and(cri, Restrictions.like("deliveryNo", form.deliveryNo, MatchMode.ANYWHERE));
        cri = Restrictions.and(cri,
                //Restrictions.eq("type",form.orderType),
                Restrictions.eq("schoolId",CurrentUser.get().schoolId),
                Restrictions.eq("orderStatus",7));
        pageQuery.hibernateCriterion=cri;
        pageQuery.order="desc";
        pageQuery.sort="updateDate";
        PageList<ExpReceiveOrder> list = hibernateReadonlyRepository.getList(ExpReceiveOrder.class, pageQuery);
        return  list;
    }

    /**
     * @Description: 拒收订单详情
     * @Author: ,disvenk.dai
     * @Date: 2018/1/13
     */

    public ExpReceiveOrder findRefuse(Integer id){
        return get2(ExpReceiveOrder.class,"id",id);
    }

   /* *//**
     * @Description: 搜索拒收订单
     * @Author: ,disvenk.dai
     * @Date: 2018/1/13
     *//*
    public ExpReceiveOrder explorerRefuseOrder(String deliveryNo){
        ExpReceiveOrder expReceiveOrder = (ExpReceiveOrder) getCurrentSession().createCriteria(ExpReceiveOrder.class)
                .add(Restrictions.eq("deliveryNo",deliveryNo))
                .add(Restrictions.eq("schoolId", CurrentUser.get().schoolId))
                .add(Restrictions.eq("orderStatus", 7))
               // .add(Restrictions.eq("delivererId", CurrentUser.get().userId))
                .add(Restrictions.eq("logicDeleted",false))
                .uniqueResult();
        return  expReceiveOrder;
    }*/

    /**
     * @Description: 退回订单列表
     * @Author: ,disvenk.dai
     * @Date: 2018/1/13
     */
    @Cacheable(value = {"ExpReceiveOrder"})
    public PageList<ExpReceiveOrder> findReturnList(PageQuery pageQuery,IdForm form){
        Criterion cri = Restrictions.eq("logicDeleted", false);
        if (StringUtils.isNotBlank(form.deliveryNo) && form.deliveryNo != null) {
            cri = Restrictions.and(cri, Restrictions.like("deliveryNo", form.deliveryNo, MatchMode.ANYWHERE));
        }
        cri = Restrictions.and(cri,
                //Restrictions.eq("type",form.orderType),
                Restrictions.eq("schoolId",CurrentUser.get().schoolId),
                Restrictions.eq("orderStatus",8));
        pageQuery.hibernateCriterion=cri;
        pageQuery.order="desc";
         pageQuery.sort="updateDate";
        PageList<ExpReceiveOrder> list = hibernateReadonlyRepository.getList(ExpReceiveOrder.class, pageQuery);
        return  list;
    }
    /**
     * @Description: 退回订单详情
     * @Author: ,disvenk.dai
     * @Date: 2018/1/13
     */

    public ExpReceiveOrder findReturn(Integer id){
        return get2(ExpReceiveOrder.class,"id",id);
    }

    /**
     * @Description: 快件查询
     * @Author: disvenk.dai
     * @Date: 2018/1/18
     */
    public PageList<ExpReceiveOrder> findReceiveList(PageQuery pageQuery,ExpressQueryForm form){
        Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted",false)
                ,Restrictions.eq("schoolId", CurrentUser.get().schoolId));

        Criterion cri2 = null;
        if(form.deliveryNo!=null && StringUtils.isNotBlank(form.deliveryNo)){
           cri =Restrictions.and(cri,Restrictions.like("deliveryNo",form.deliveryNo,MatchMode.ANYWHERE));
        }
        if(form.mobile!=null && StringUtils.isNotBlank(form.mobile)){
          List<ExpUser> users = getCurrentSession().createCriteria(ExpUser.class).add(Restrictions.like("userMobile",form.mobile,MatchMode.ANYWHERE)).list();
          List<Integer> userIds = new ArrayList<>();
          for(ExpUser expUser : users){
              userIds.add(expUser.getId());
          }
            cri =Restrictions.and(cri,Restrictions.in("userId",userIds));
        }
        pageQuery.hibernateCriterion=cri;
        pageQuery.order="desc";
        pageQuery.sort="updateDate";
        PageList<ExpReceiveOrder> expReceiveOrderList = hibernateReadonlyRepository.getList(ExpReceiveOrder.class,pageQuery);
        return expReceiveOrderList;
    }

    /**
     * @Description: 查询账单表
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    public Payment getPayment(Integer orderId){
        Payment payment = get2(Payment.class,"orderId",orderId,"status",20,"type",1);
        return payment;
    }

    /**
     * @Description: 查询详情
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    public ExpReceiveOrder getReceiveOrder(Integer orderId) throws ResponseEntityException {
        ExpReceiveOrder expReceiveOrder = get2(ExpReceiveOrder.class, "id", orderId);
        if(expReceiveOrder==null){
            throw new ResponseEntityException(220,"该订单不存在");
        }
        return expReceiveOrder;
    }

    /**
     *@Description: 快递人员自行签收
     *@Author: disvenk.dai
     *@Date: 上午 11:01 2018/2/23 0023
     */
    public ExpReceiveOrder refuseByXiaoGe(Integer id) throws ResponseEntityException, UpsertException {
        ExpReceiveOrder expReceiveOrder = get2(ExpReceiveOrder.class,"id",id);
        if(expReceiveOrder==null || (expReceiveOrder.getOrderStatus()!=2 && expReceiveOrder.getOrderStatus()!=4)){
            throw new ResponseEntityException(220,"该订单不存在或不是已到达或不是已接单");
        }
        expReceiveOrder.setOrderStatus(7);
        return upsert2(expReceiveOrder);
    }
}
