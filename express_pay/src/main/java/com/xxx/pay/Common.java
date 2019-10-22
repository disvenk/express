package com.xxx.pay;

public class Common {
    /**
     * 订单类型验证
     *
     * @return
     */
    public static boolean validateOrderType(int orderType) {
        if (orderType >= 1 )
            return true;
        else
            return false;
    }

    /**
     * 支付渠道验证
     *
     * @param channel 支付渠道, ALIPAY:支付宝; WECHAT:微信; UPMP:银联 CASH:现金
     * @return
     */
    public static boolean validatePayChannel(String channel) {
        if (channel.equals("ALIPAY") || channel.equals("WECHAT") || channel.equals("UPMP") || channel.equals("CASH")|| channel.equals("CASHALIPAY")|| channel.equals("CASHWECHAT"))
            return true;
        else
            return false;
    }
}
