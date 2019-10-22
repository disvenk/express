package com.xxx.pay.weixin;

/**
 * Created by wanghua on 17/3/17.
 * 交易类型
 */
public enum TradeType {
    JSAPI,     //公众号支付
    NATIVE,    //原生扫码支付
    APP,       //app支付
    MICROPAY   //刷卡支付，刷卡支付有单独的支付接口，不调用统一下单接口
}
