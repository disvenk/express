package com.xxx.pay.form;

/**
 * Created by wanghua on 17/3/17.
 */
public class UnifyOrderForm extends PayForm {
    public String tradeType;    //【微信,必填】取值如下：JSAPI，NATIVE，APP等
    public String productId;    //【微信】trade_type=NATIVE时（即扫码支付），此参数必传。此参数为二维码中包含的商品ID，商户自行定义
    public String openid;       //【微信】trade_type=JSAPI时（即公众号支付），此参数必传，此参数为微信用户在商户对应appid下的唯一标识。
    public String body;         //【通用-必填】商品，支付成功后显示
    public String subject;      //【支付宝-必填】订单标题
    public String returnUrl;    //【支付宝】回调页面跳转路径

    @Override
    public String toString() {
        return "UnifyOrderForm{" +
                "tradeType='" + tradeType + '\'' +
                ", productId='" + productId + '\'' +
                ", openid='" + openid + '\'' +
                ", body='" + body + '\'' +
                ", subject='" + subject + '\'' +
                ", returnUrl='" + returnUrl + '\'' +
                ", orderId='" + orderId + '\'' +
                ", orderType='" + orderType + '\'' +
                ", outTradeNo='" + outTradeNo + '\'' +
                ", totalFee='" + totalFee + '\'' +
                '}';
    }
}
