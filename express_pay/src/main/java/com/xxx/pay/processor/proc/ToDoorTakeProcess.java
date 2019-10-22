package com.xxx.pay.processor.proc;

import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.core.service.CommonService;
import com.xxx.core.spring.SpringContext;
import com.xxx.model.business.*;
import com.xxx.pay.processor.OrderProcess;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * @Description: 门店采购订单支付确认
 * @Author: Chen.zm
 * @Date: 2017/11/7 0007
 */
public class ToDoorTakeProcess implements OrderProcess {
    private int id;
    private String payNo;
    private String channel;
    private CommonService commonServic;

    public ToDoorTakeProcess(int targetOrderId, String payNo, String channel) {
        this.id = targetOrderId;
        this.payNo = payNo;
        this.channel = channel;
        this.commonServic = (CommonService) SpringContext.getApplicationContext().getBean("commonService");
    }



    @Override
    public void process() throws UpsertException {
        ExpSendOrder expSendOrder  = commonServic.get2(ExpSendOrder.class, id);
        Payment payment = commonServic.get2(Payment.class, "payNo", payNo);

        expSendOrder.setOrderStatus(3);
        expSendOrder.setPayStatus(1);//修改订单的支付状态
        commonServic.upsert2(expSendOrder);

        try {
            ExpSchool exp = commonServic.get2(ExpSchool.class,"id", expSendOrder.getSchoolId(),"logicDeleted",false);

            if(exp.getIspaper() == 0){
                List<ExpDeliveryOrderNo> expOrderNoList = selectSendOrderDeliverNo(expSendOrder.getSchoolId(), expSendOrder.getDeliveryId());
                if(expOrderNoList.size() == 0 || expOrderNoList == null){
                    return;
                }
                updateSendOrderDeliverNo(expSendOrder.getId(), expOrderNoList.get(0).getDeliveryNo(),expSendOrder.getSchoolId(),expSendOrder.getDeliveryId());
            }
        } catch (Exception e) {

        }

    }

    public List<ExpDeliveryOrderNo> selectSendOrderDeliverNo(Integer schoolId, Integer deliveryId) {
        Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
        cri = Restrictions.and(cri, Restrictions.eq("schoolId", schoolId));
        cri = Restrictions.and(cri, Restrictions.eq("deliveryId", deliveryId));
        cri = Restrictions.and(cri, Restrictions.eq("isUsed", false));
        return (List<ExpDeliveryOrderNo>) commonServic.getCurrentSession().createCriteria(ExpDeliveryOrderNo.class)
                .add(cri)
                .list();
    }

    public ExpSendOrder updateSendOrderDeliverNo(Integer id,String deliveryNo,Integer schoolId, Integer deliveryId) throws UpsertException,ResponseEntityException {
        Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
        cri = Restrictions.and(cri, Restrictions.eq("deliveryNo", deliveryNo));
        cri = Restrictions.and(cri, Restrictions.ne("id",id));
        ExpSendOrder exp = (ExpSendOrder) commonServic.getCurrentSession().createCriteria(ExpSendOrder.class)
                .add(cri)
                .uniqueResult();
        if(exp != null)
            throw new ResponseEntityException(210, "该订单的快递单号已被其他订单使用");
        ExpDeliveryOrderNo expOrderNo = commonServic.get2(ExpDeliveryOrderNo.class,"deliveryNo",deliveryNo,"isUsed",false,"logicDeleted",false);
        if(expOrderNo == null){
            throw new ResponseEntityException(220,"快递单号已被使用或系统没维护该快递单号");
        }
        ExpSendOrder exp1 = commonServic.get2(ExpSendOrder.class,"id",id,"logicDeleted",false);
        exp1.setDeliveryNo(deliveryNo);
        expOrderNo.setUsed(true);
        expOrderNo.setDeliveryId(deliveryId);
        expOrderNo.setSchoolId(schoolId);
        expOrderNo.setDeliveryNo(deliveryNo);
        commonServic.upsert2(expOrderNo);
        return commonServic.upsert2(exp1);
    }


}
