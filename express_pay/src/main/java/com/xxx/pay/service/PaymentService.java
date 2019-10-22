package com.xxx.pay.service;

import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.Payment;
import com.xxx.pay.form.PayForm;
import com.xxx.pay.processor.OrderPreProcessFactory;
import com.xxx.pay.processor.OrderProcessFactory;
import com.xxx.utils.UUIDGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 */
@Service
public class PaymentService extends CommonService {
    private static final Lock lock = new ReentrantLock();

    /**
     * 如果交易号不存在，则插入一条流水，并返回支付号到 PayForm
     *
     * @param payForm payForm
     * @param channel 支付渠道, ALIPAY:支付宝; WECHAT:微信; UPMP:银联; CASH:现金
     * @param type    1:线下支付，0或者null为线上
     * @throws Exception
     */
    public Payment createPayment(PayForm payForm, String channel, int type) throws Exception {
        System.out.println("----------  进入createPayment ： " + payForm.toString() + " ----------");
        if (StringUtils.isBlank(payForm.outTradeNo)) {
            Payment payment = new Payment();
            payment.setPayNo(UUIDGenerator.generatorUUID32());
            payment.setOrderId(payForm.orderId == null ? null : payForm.orderId);
            payment.setOrderType(payForm.orderType);
            payment.setPrice(payForm.totalFee * 1d/100);
            payment.setStatus(10);
            payment.setCreateTime(new Long(String.valueOf(System.currentTimeMillis() / 1000)));
            payment.setChannel(channel);
            payment.setType(type);
            payment = upsert2(payment);
            payForm.outTradeNo = payment.getPayNo();

            int targetOrderId = payment.getOrderId();
            double price = payment.getPrice();
            new OrderPreProcessFactory().create(targetOrderId, payment.getOrderType(), price).process();

            System.out.println("---------- 校验后的createPayment ： " + payForm.toString() + " ----------");
            return payment;
        }
        return null;
    }

    /**
     * 支付成功，处理后续业务
     * @param outTradeNo
     * @throws UpsertException
     */
    public void FinishPayment(String outTradeNo) throws Exception {
        System.out.println("---------- 支付回调 ： " + outTradeNo + "  -------------");
        lock.lock();
        try {
            //当收到通知进行处理时，首先检查对应业务数据的状态，判断该通知是否已经处理过，并进行加锁控制
            Payment payment = get2(Payment.class, "payNo", outTradeNo);
            if (!payment.getStatus().equals(20)) {
                payment.setStatus(20);
                payment.setFinishTime(new Long(String.valueOf(System.currentTimeMillis() / 1000)));
                upsert2(payment);

                int targetOrderId = payment.getOrderId();
                new OrderProcessFactory().create(targetOrderId, payment.getOrderType(), outTradeNo, payment.getChannel()).process();
            }
        } finally {
            lock.unlock();
        }
    }
}
