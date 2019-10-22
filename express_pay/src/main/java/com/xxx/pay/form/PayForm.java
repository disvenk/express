package com.xxx.pay.form;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * 共用支付参数
 */
public class PayForm implements Serializable {
    public Integer orderId;         //订单id
    public Integer orderType;       //订单类型
    public String outTradeNo;   //订单号，必须唯一。支付失败后，重新支付需要使用同一个订单号
    public int totalFee;        //订单总金额，单位为分，1角=10分
    public JSONObject attach;       //附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用。

    @Override
    public String toString() {
        return "PayForm{" +
                "orderId=" + orderId +
                ", orderType=" + orderType +
                ", outTradeNo='" + outTradeNo + '\'' +
                ", totalFee=" + totalFee +
                ", attach=" + attach +
                '}';
    }
}
