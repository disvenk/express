package com.xxx.pay.processor.proc;

import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.service.CommonService;
import com.xxx.core.spring.SpringContext;
import com.xxx.model.business.*;
import com.xxx.pay.processor.OrderProcess;
import com.xxx.utils.Arith;
import com.xxx.utils.DateTimeUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * @Description: 门店采购订单支付确认
 * @Author: Chen.zm
 * @Date: 2017/11/7 0007
 */
public class TakeToDoorProcess implements OrderProcess {
    private int id;
    private String payNo;
    private String channel;
    private CommonService commonServic;

    public TakeToDoorProcess(int targetOrderId, String payNo, String channel) {
        this.id = targetOrderId;
        this.payNo = payNo;
        this.channel = channel;
        this.commonServic = (CommonService) SpringContext.getApplicationContext().getBean("commonService");
    }

    @Override
    public void process() throws UpsertException {
        ExpReceiveOrder expReceiveOrder = commonServic.get2(ExpReceiveOrder.class, id);
        Payment payment = commonServic.get2(Payment.class, "payNo", payNo);

        expReceiveOrder.setPayStatus(1);//修改订单的支付状态
        expReceiveOrder.setOrderStatus(3);
        expReceiveOrder.setType(1);
        commonServic.upsert2(expReceiveOrder);
    }


}
