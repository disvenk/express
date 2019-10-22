package com.xxx.merchant.service;

import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.core.service.CommonService;
import com.xxx.merchant.form.SendOrderForm;
import com.xxx.merchant.form.SendOrderGoDoorForm;
import com.xxx.model.business.ExpDeliveryCompany;
import com.xxx.model.business.ExpDeliveryOrderNo;
import com.xxx.model.business.ExpProvinceServiceFee;
import com.xxx.model.business.ExpSendOrder;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("SendExpressOrderService2")
public class SendExpressOrderService extends CommonService{

    /**
     * @Description: 寄件订单
     * @Author: disvenk.dai
     * @Date: 2018/1/12
     */
//    @CacheEvict(value = "ExpSendOrder",allEntries = true)
//    public ExpSendOrder saveSendExpressOrder(SendExpressForm form){
//        ExpSendOrder expSendOrder = new ExpSendOrder();
//        expSendOrder.setSchoolId(CurrentUser.get().schoolId);
//        expSendOrder.setUserId(CurrentUser.get().userId);
//        //查询指定网点的快递公司的没有使用的快递单号
//        //ExpUser expUser = get2(ExpUser.class,CurrentUser.get().userId);
//        List<ExpDeliveryOrderNo> list = getCurrentSession().createCriteria(ExpDeliveryOrderNo.class)
//                .add(Restrictions.eq("schoolId",CurrentUser.get().schoolId))
//                .add(Restrictions.eq("deliveryId",form.expressCompanyId))
//                .add(Restrictions.eq("isUsed",false))
//                .add(Restrictions.eq("logicDeleted",false))
//                .list();
//
//        expSendOrder.setDeliveryNo(list.get(0).getDeliveryNo());
//        expSendOrder.setType(form.sendType);
//        if(form.sendType==1){
//            expSendOrder.setOrderStatus(1);
//        }else {
//            expSendOrder.setOrderStatus(2);
//        }
//        expSendOrder.setReceiverName(form.recievePname);
//        expSendOrder.setReceiverTel(form.revievePhone);
//        expSendOrder.setReceiverAddress(form.receiveAddress);
//        expSendOrder.setSendName(form.sendPname);
//        expSendOrder.setSendTel(form.sendPhone);
//        expSendOrder.setSendAddress(form.sendAddress);
//        expSendOrder.setReceiverProvince(form.proviceName);
//        expSendOrder.setReceiverCity(form.cityName);
//        expSendOrder.setReceiverZone(form.areaName);
//        expSendOrder.setOrderNo(GenerateNumberUtil.generateSendExpressNumber());
//        expSendOrder.setGoodsTypeId(form.goodsTypeId);
//        expSendOrder.setGoodsTypeName(form.goodsTypeName);
//        expSendOrder.setGoodsDescription(form.desc);
//        //expSendOrder.setProvinceId(form.provinceId);
//
//        try {
//            if(form.sendType==1){
//                expSendOrder.setTodoorDate(DateTimeUtils.parseDate(form.doorTime,"yyyy-MM-dd HH:mm:ss"));
//            }
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        expSendOrder.setDeliveryId(form.expressCompanyId);
//        ExpSendOrder expSendOrder1=null;
//        try {
//            expSendOrder1 = upsert2(expSendOrder);
//        } catch (UpsertException e) {
//            e.printStackTrace();
//        }
//        return expSendOrder1;
//    }

    /**
     * @Description: 寄件订单列表
     * @Author: disvenk
     * @Date: 2018/1/13
     */
    public List<ExpSendOrder> findSendOrderList(Integer userId, Integer schoolId, Integer orderType) {
        Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
        cri = Restrictions.and(cri, Restrictions.eq("userId", userId));
        cri = Restrictions.and(cri, Restrictions.eq("schoolId", schoolId));
        cri = Restrictions.and(cri, Restrictions.eq("type", orderType));
        return getCurrentSession().createCriteria(ExpSendOrder.class)
                .add(cri)
                .addOrder(Order.desc("createdDate"))
                .list();
    }

    /**
     * @Description: 查询单个寄件订单
     * @Author: disvenk
     * @Date: 2018/1/13
     */
    public ExpSendOrder findSendOrder(Integer id) {
        ExpSendOrder expSendOrder = get2(ExpSendOrder.class,id);
        return expSendOrder;
    }

    /**
     * @Description:寄件订单的自行寄件
     * @Author: hanchao
     * @Date: 2018/1/17 0017
     */
    public PageList<ExpSendOrder> findSendOrderByMySelfList(PageQuery pageQuery,Integer userId, Integer schoolId, Integer type, String receiverName) {
        Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
//        cri = Restrictions.and(cri, Restrictions.eq("userId", userId));
        cri = Restrictions.and(cri, Restrictions.eq("schoolId", schoolId));
        cri = Restrictions.and(cri, Restrictions.in("orderStatus", 1,2,3));

        if(type != null){
            cri = Restrictions.and(cri, Restrictions.eq("type", type));
        }
        if(StringUtils.isNotBlank(receiverName)){
           cri = Restrictions.and(cri, Restrictions.or(Restrictions.like("receiverName",receiverName,MatchMode.ANYWHERE),Restrictions.like("receiverTel",receiverName,MatchMode.ANYWHERE)));
        }
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "createdDate";
        PageList<ExpSendOrder> list = hibernateReadonlyRepository.getList(ExpSendOrder.class, pageQuery);
        return list;
    }

    /**
     * @Description:寄件订单的上门取件
     * @Author: hanchao
     * @Date: 2018/1/17 0017
     */
    public PageList<ExpSendOrder> findSendOrderByToDoorMySelfList(PageQuery pageQuery,Integer userId, Integer schoolId, Integer type, String receiverName,Integer status) {
        Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
        cri = Restrictions.and(cri, Restrictions.eq("schoolId", schoolId));
        if(status != null){
            cri = Restrictions.and(cri, Restrictions.eq("delivererId",userId));
            cri = Restrictions.and(cri, Restrictions.in("orderStatus", 1,2,3));
        }else{
            cri = Restrictions.and(cri, Restrictions.in("orderStatus", 1));
            cri = Restrictions.and(cri, Restrictions.isNull("delivererId"));
        }
        if(type != null){
            cri = Restrictions.and(cri, Restrictions.eq("type", type));
        }
        if(StringUtils.isNotBlank(receiverName)){
           cri = Restrictions.and(cri, Restrictions.or(Restrictions.like("receiverName",receiverName,MatchMode.ANYWHERE),Restrictions.like("receiverTel",receiverName,MatchMode.ANYWHERE)));
        }
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "createdDate";
        PageList<ExpSendOrder> list = hibernateReadonlyRepository.getList(ExpSendOrder.class, pageQuery);
        return list;
    }

    /**
     * @Description:寄件订单的自行寄件(待取件,已支付)详情
     * @Author: hanchao
     * @Date: 2018/1/17 0017
     */
    public ExpSendOrder selectSendOrderByMySelfDetail(Integer schoolId, Integer id,Integer orderStatus) {
        Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
        cri = Restrictions.and(cri, Restrictions.eq("schoolId", schoolId));
        cri = Restrictions.and(cri, Restrictions.eq("id",id));
        if(orderStatus != null){
            cri = Restrictions.and(cri, Restrictions.eq("orderStatus", orderStatus));
        }
        return (ExpSendOrder) getCurrentSession().createCriteria(ExpSendOrder.class)
                .add(cri)
                .addOrder(Order.desc("createdDate"))
                .uniqueResult();
    }

    /**
     * @Description:寄件订单的自行寄件待取件确认收件
     * @Author: hanchao
     * @Date: 2018/1/18 0018
     */
    public ExpSendOrder updateSendOrderByMySelf(SendOrderForm form) throws UpsertException,ResponseEntityException {
        ExpSendOrder exp = get2(ExpSendOrder.class,"id",form.id,"logicDeleted",false);
        if(exp == null)
            throw new ResponseEntityException(210, "该订单信息不存在");
        if(StringUtils.isBlank(form.deliveryName)){
            throw new ResponseEntityException(220, "快递公司名称为空");
        }
        ExpDeliveryCompany expDeliveryCompany = get2(ExpDeliveryCompany.class,"name",form.deliveryName,"logicDeleted",false);
        exp.setGoodsTypeName(form.goodsTypeName);
        exp.setDeliveryId(expDeliveryCompany.getId());
        exp.setWeight(form.weight);
        exp.setOrderStatus(2);
        exp.setCost(form.costPrice);//成本
        exp.setExpressDate(new Date());
        exp.setPrice(form.price);//快递费
        exp.setOrderPrice(form.price);//订单费==快递价格+上门费
        exp.setCost(form.costPrice);//成本
        return upsert2(exp);
    }

    /**
     * @Description:寄件订单的上门取件我要上门
     * @Author: hanchao
     * @Date: 2018/1/18 0018
     */
        public ExpSendOrder updateSendOrderByGoDoor(Integer id , Integer delivererId) throws UpsertException,ResponseEntityException {
        ExpSendOrder exp = get2(ExpSendOrder.class,"id",id,"logicDeleted",false);
        if(exp == null)
            throw new ResponseEntityException(210, "该订单不存在或已被别人取件");
        exp.setDelivererId(delivererId);
//        exp.setRemarks(form.remark);
//        exp.setGoodsTypeName(form.goodsTypeName);
//        exp.setDeliveryId(form.deliveryId);
//        exp.setWeight(form.weight);
//        exp.setReceiverProvince(form.receiveProvince);
//        exp.setReceiverCity(form.receiveCity);
//        exp.setReceiverZone(form.receiveZone);
//        exp.setSendProvince(form.sendProvince);
//        exp.setSendCity(form.sendCity);
//        exp.setSendZone(form.sendZone);
        return upsert2(exp);
    }

    /**
     * @Description:寄件订单的确认取件
     * @Author: hanchao
     * @Date: 2018/1/18 0018
     */
    public ExpSendOrder updateSendOrderByReceive(Integer id) throws UpsertException,ResponseEntityException {
        ExpSendOrder exp = get2(ExpSendOrder.class,"id",id,"logicDeleted",false);
        if(exp == null)
            throw new ResponseEntityException(210, "该订单信息不存在");
        exp.setOrderStatus(2);
        exp.setExpressDate(new Date());
        return upsert2(exp);
    }

    /**
     * @Description:寄件订单的上门取件详情的确认取件
     * @Author: hanchao
     * @Date: 2018/1/29 0029
     */
    public ExpSendOrder updateSendOrderDetailByReceive(SendOrderGoDoorForm form) throws UpsertException,ResponseEntityException {
        ExpSendOrder exp = get2(ExpSendOrder.class,"id",form.id,"logicDeleted",false);
        if(exp == null)
            throw new ResponseEntityException(210, "该订单信息不存在");
        ExpDeliveryCompany expDeliveryCompany = get2(ExpDeliveryCompany.class,"name",form.deliveryName,"logicDeleted",false);
        if(expDeliveryCompany == null){
            throw new ResponseEntityException(220, "快递公司不存在");
        }
        exp.setOrderStatus(2);
        exp.setGoodsTypeName(form.goodsTypeName);
        exp.setDeliveryId(expDeliveryCompany.getId());
        exp.setWeight(form.weight);
        exp.setExpressDate(new Date());
        Double price = form.price - (form.todoorFee == null ? 0 :form.todoorFee) + (exp.getTodoorTipFee() == null ? 0 : exp.getTodoorTipFee());
        exp.setPrice(price);//快递费
        exp.setTodoorFee(form.todoorFee);//上门费
        exp.setOrderPrice(form.price);//总费
        exp.setCost(form.costPrice);//成本
        return upsert2(exp);
    }

    /**
     * @Description:根据物品重量和快递公司计算运费
     * @Author: hanchao
     * @Date: 2018/1/18 0018
     */
    public ExpProvinceServiceFee sendOrderPriceByWeightAndDelivery(Integer id,String provinceName) {
        Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
        cri = Restrictions.and(cri, Restrictions.eq("schoolDeliveryCompanyId", id));
        cri = Restrictions.and(cri, Restrictions.eq("provinceName", provinceName));
        return (ExpProvinceServiceFee) getCurrentSession().createCriteria(ExpProvinceServiceFee.class)
                .add(cri)
                .uniqueResult();
    }

    /**
     * @Description:寄件订单保存电子面单快递单号
     * @Author: hanchao
     * @Date: 2018/1/31 0031
     */
    public ExpSendOrder updateSendOrderDeliverNo(Integer id,String deliveryNo,Integer schoolId, Integer deliveryId) throws UpsertException,ResponseEntityException {
        Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
        cri = Restrictions.and(cri, Restrictions.eq("deliveryNo", deliveryNo));
        cri = Restrictions.and(cri, Restrictions.ne("id",id));
        ExpSendOrder exp = (ExpSendOrder) getCurrentSession().createCriteria(ExpSendOrder.class)
                .add(cri)
                .uniqueResult();
        if(exp != null)
            throw new ResponseEntityException(210, "该订单的快递单号已被其他订单使用");
        ExpDeliveryOrderNo expOrderNo = get2(ExpDeliveryOrderNo.class,"deliveryNo",deliveryNo,"isUsed",false,"logicDeleted",false);
        if(expOrderNo == null){
            throw new ResponseEntityException(220,"快递单号已被使用或系统没维护该快递单号");
        }
        ExpSendOrder exp1 = get2(ExpSendOrder.class,"id",id,"logicDeleted",false);
        exp1.setDeliveryNo(deliveryNo);
        expOrderNo.setUsed(true);
//        expOrderNo.setDeliveryId(deliveryId);
//        expOrderNo.setSchoolId(schoolId);
//        expOrderNo.setDeliveryNo(deliveryNo);
        upsert2(expOrderNo);
        return upsert2(exp1);
    }

    /**
     * @Description:寄件订单保存纸质面单快递单号
     * @Author: hanchao
     * @Date: 2018/1/31 0031
     */
    public ExpSendOrder updateSendOrderDeliverNoIspaper(Integer id,String deliveryNo) throws UpsertException,ResponseEntityException {
        Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
        cri = Restrictions.and(cri, Restrictions.eq("deliveryNo", deliveryNo));
        cri = Restrictions.and(cri, Restrictions.ne("id",id));
        ExpSendOrder exp = (ExpSendOrder) getCurrentSession().createCriteria(ExpSendOrder.class)
                .add(cri)
                .uniqueResult();
        if(exp != null)
            throw new ResponseEntityException(210, "该订单的快递单号已被其他订单使用");
        exp.setDeliveryNo(deliveryNo);
        return upsert2(exp);
    }


    /**
     * @Description:根据网点和快递公司查询未使用的快递单
     * @Author: hanchao
     * @Date: 2018/1/31 0031
     */
    public List<ExpDeliveryOrderNo> selectSendOrderDeliverNo(Integer schoolId, Integer deliveryId) {
        Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
        cri = Restrictions.and(cri, Restrictions.eq("schoolId", schoolId));
        cri = Restrictions.and(cri, Restrictions.eq("deliveryId", deliveryId));
        cri = Restrictions.and(cri, Restrictions.eq("isUsed", false));
        return (List<ExpDeliveryOrderNo>) getCurrentSession().createCriteria(ExpDeliveryOrderNo.class)
                .add(cri)
                .list();
    }

}
