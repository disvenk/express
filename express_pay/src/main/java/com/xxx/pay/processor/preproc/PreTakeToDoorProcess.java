package com.xxx.pay.processor.preproc;

import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.service.CommonService;
import com.xxx.core.spring.SpringContext;
import com.xxx.model.business.ExpReceiveOrder;
import com.xxx.pay.processor.OrderProcess;
import org.hibernate.criterion.Restrictions;

/**
 * @Description: 上门派件订单支付前校验
 * @Author: Chen.zm
 * @Date: 2017/11/7 0007
 */
public class PreTakeToDoorProcess implements OrderProcess {
    private int id;
    private double price;
    private CommonService commonServic;

    public PreTakeToDoorProcess(int targetOrderId, double price) {
        this.id = targetOrderId;
        this.price = price;
        this.commonServic = (CommonService) SpringContext.getApplicationContext().getBean("commonService");
    }

    @Override
    public void process() throws Exception {
        ExpReceiveOrder expReceiveOrder = (ExpReceiveOrder) commonServic.getCurrentSession().createCriteria(ExpReceiveOrder.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();

        if (expReceiveOrder == null)
            throw new ResponseEntityException(110, "订单不存在");
        if (expReceiveOrder.getPayStatus() != null && !expReceiveOrder.getPayStatus().equals(0))
            throw new ResponseEntityException(120, "订单状态不是待支付");

        if (expReceiveOrder.getOrderPrice() == null || !expReceiveOrder.getOrderPrice().equals(price))
            throw new ResponseEntityException(130, "支付金额不正确");

    }
}
