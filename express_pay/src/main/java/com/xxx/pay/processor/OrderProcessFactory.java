package com.xxx.pay.processor;

import com.xxx.pay.processor.proc.TakeToDoorProcess;
import com.xxx.pay.processor.proc.ToDoorTakeProcess;

/**
 * 订单处理工厂
 */
public class OrderProcessFactory {
    public OrderProcess create(int targetOrderId, int orderType, String payNo, String channel) {
        switch (orderType) {  // 1-上门派件订单   2-上门取件定单
            case 1:
                return new TakeToDoorProcess(targetOrderId, payNo, channel);
            case 2:
                return new ToDoorTakeProcess(targetOrderId, payNo, channel);
        }
        return null;
    }
}
