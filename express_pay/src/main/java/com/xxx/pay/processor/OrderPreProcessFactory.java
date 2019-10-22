package com.xxx.pay.processor;


import com.xxx.pay.processor.preproc.PreTakeToDoorProcess;
import com.xxx.pay.processor.preproc.PreToDoorTakeProcess;

/**
 * 订单处理工厂
 */
public class OrderPreProcessFactory {
    public OrderProcess create(int targetOrderId, int orderType, double price) {
        switch (orderType) {  // 1-上门派件订单   2-上门取件定单
            case 1:
                return new PreTakeToDoorProcess(targetOrderId, price);
            case 2:
                return new PreToDoorTakeProcess(targetOrderId, price);
        }
        return null;

    }
}
