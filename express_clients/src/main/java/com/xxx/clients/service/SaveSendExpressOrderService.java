package com.xxx.clients.service;

import com.xxx.clients.form.IdForm;
import com.xxx.clients.form.SendExpressForm;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.*;
import com.xxx.model.system.SYS_Menu;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.utils.GenerateNumberUtil;
import com.xxx.utils.DateTimeUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

@Service
public class SaveSendExpressOrderService extends CommonService{

    /**
     * @Description: 寄件订单
     * @Author: disvenk.dai
     * @Date: 2018/1/12
     */
    @CacheEvict(value = "ExpSendOrder",allEntries = true)
    public ExpSendOrder saveSendExpressOrder(SendExpressForm form){
        ExpSendOrder expSendOrder = new ExpSendOrder();
        expSendOrder.setSchoolId(CurrentUser.get().schoolId);
        expSendOrder.setUserId(CurrentUser.get().userId);
        //查询指定网点的快递公司的没有使用的快递单号
        //ExpUser expUser = get2(ExpUser.class,CurrentUser.get().userId);
       /* List<ExpDeliveryOrderNo> list = getCurrentSession().createCriteria(ExpDeliveryOrderNo.class)
                .add(Restrictions.eq("schoolId",CurrentUser.get().schoolId))
                .add(Restrictions.eq("deliveryId",form.expressCompanyId))
                .add(Restrictions.eq("isUsed",false))
                .add(Restrictions.eq("logicDeleted",false))
                .list();*/

        //expSendOrder.setDeliveryNo(list.get(0).getDeliveryNo());
        expSendOrder.setType(form.sendType);
        expSendOrder.setOrderStatus(1);
        expSendOrder.setRemarks(form.desc);
        ExpReceiveAddress expReceiveAddress = get2(ExpReceiveAddress.class,"id",form.recievePid);
        ExpSendAddress expSendAddress = get2(ExpSendAddress.class,"id",form.sendPid);
        expSendOrder.setReceiverName(expReceiveAddress.getName());
        expSendOrder.setReceiverTel(expReceiveAddress.getTel());
        String city = expReceiveAddress.getCity()==null?"":expReceiveAddress.getCity();
        String zone = expReceiveAddress.getZone()==null?"":expReceiveAddress.getZone();
        expSendOrder.setReceiverAddress(expReceiveAddress.getAddress());

        expSendOrder.setSendName(expSendAddress.getName());
        expSendOrder.setSendTel(expSendAddress.getTel());
        expSendOrder.setSendProvince(expSendAddress.getProvince());
        String cityName = expSendAddress.getCity()==null?"":expSendAddress.getCity();
        String zoneName = expSendAddress.getZone()==null?"":expSendAddress.getZone();
        expSendOrder.setSendCity(cityName);
        expSendOrder.setSendZone(zoneName);
        expSendOrder.setSendAddress(expSendAddress.getAddress());

        String cityName1 = expReceiveAddress.getCity()==null?"":expReceiveAddress.getCity();
        String zoneName2 = expReceiveAddress.getZone()==null?"":expReceiveAddress.getZone();
        expSendOrder.setReceiverProvince(expReceiveAddress.getProvince());
        expSendOrder.setReceiverCity(cityName1);
        expSendOrder.setReceiverZone(zoneName2);

        expSendOrder.setOrderNo(GenerateNumberUtil.generateSendExpressNumber());
        expSendOrder.setGoodsTypeId(form.goodsTypeId);
       ExpGoodsType expGoodsType = get2(ExpGoodsType.class,"id",form.goodsTypeId);
        expSendOrder.setGoodsTypeName(expGoodsType.getName());
        expSendOrder.setRemarks(form.desc);
        expSendOrder.setTodoorBeginEND(form.doorTime);
        expSendOrder.setDeliveryId(form.expressCompanyId);
        ExpSendOrder expSendOrder1=null;
        try {
            expSendOrder1 = upsert2(expSendOrder);
        } catch (UpsertException e) {
            e.printStackTrace();
        }
        return expSendOrder1;
    }

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
     * @Description: 查询账单表
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    public Payment getPayment(Integer orderId){
        Payment payment = get2(Payment.class,"orderId",orderId,"orderType",2,"status",20);
        return payment;
    }

    /**
     * @Description: 取消订单
     * @Author: disvenk.dai
     * @Date: 2018/1/19
     */
    public ExpSendOrder cancelOrder(Integer id) throws ResponseEntityException, UpsertException {
       ExpSendOrder expSendOrder = get2(ExpSendOrder.class,"id",id);
       if(expSendOrder==null){
           throw new ResponseEntityException(220,"该订单不存在");
       }
       expSendOrder.setOrderStatus(4);
       return upsert2(expSendOrder);
    }

    /**
     * @Description: 删除订单
     * @Author: disvenk.dai
     * @Date: 2018/1/19
     */
    public ExpSendOrder deleteOrder(Integer id) throws ResponseEntityException, UpsertException {
        ExpSendOrder expSendOrder = get2(ExpSendOrder.class,"id",id);
        if(expSendOrder==null){
            throw new ResponseEntityException(220,"该订单不存在");
        }
        expSendOrder.setLogicDeleted(true);
        return upsert2(expSendOrder);
    }

    /**
     * @Description:寄件订单保存快递单号
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
        exp.setDeliveryNo(deliveryNo);
        expOrderNo.setUsed(true);
        expOrderNo.setDeliveryId(deliveryId);
        expOrderNo.setSchoolId(schoolId);
        expOrderNo.setDeliveryNo(deliveryNo);
        upsert2(expOrderNo);
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
