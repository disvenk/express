package com.xxx.pay.weixin;

import com.xxx.pay.weixin.config.Configs;
import com.xxx.utils.UUIDGenerator;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by wanghua on 17/3/17.
 */
public class UnifyOrder {
    public String key;          //签名Key
    public String appid;        //微信支付分配的公众账号ID（企业号corpid即为此appId）
    public String mchId;        //微信支付分配的商户号
    public String nonceStr;     //随机字符串，长度要求在32位以内。
    public String notifyUrl;    //异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数

    public String outTradeNo;   //订单号，必须唯一。支付失败后，重新支付需要使用同一个订单号
    public int totalFee;        //订单总金额，单位为分，1角=10分
    public String tradeType;    //取值如下：JSAPI，NATIVE，APP等
    public String productId;    //trade_type=NATIVE时（即扫码支付），此参数必传。此参数为二维码中包含的商品ID，商户自行定义
    public String openid;       //trade_type=JSAPI时（即公众号支付），此参数必传，此参数为微信用户在商户对应appid下的唯一标识。
    public String body;         //商品，支付成功后显示
    public String attach;       //附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用。

    private UnifyOrder() {
    }

    public UnifyOrder(String outTradeNo, int totalFee, String tradeType, String productId, String openid, String body, String attach) {
        this.key = Configs.key;
        this.appid = Configs.payAppid;
        this.mchId = Configs.mchId;
        this.notifyUrl = Configs.notifyUrl;
        this.nonceStr = UUIDGenerator.generatorUUID32();
        this.outTradeNo = outTradeNo;
        this.totalFee = totalFee;
        this.tradeType = tradeType;
        this.productId = productId;
        this.openid = openid;
        this.body = body;
        this.attach = attach;
    }

    /**
     * 构件统一下单接口要使用的参数
     *
     * @return
     */
    public SortedMap<String, Object> buildParams() {
        SortedMap<String, Object> parameters = new TreeMap<String, Object>();
        parameters.put("appid", this.appid);
        parameters.put("mch_id", this.mchId);
        parameters.put("openid", this.openid);
        parameters.put("body", this.body);
        parameters.put("total_fee", this.totalFee);
        parameters.put("out_trade_no", this.outTradeNo);
        parameters.put("nonce_str", this.nonceStr);
        parameters.put("notify_url", this.notifyUrl);
        parameters.put("trade_type", this.tradeType);
        parameters.put("attach", this.attach);
        parameters.put("sign", WeixinUtil.createPaySign(parameters, this.key));  //签名
        return parameters;
    }

    /**
     * 交易类型格式验证
     *
     * @param tradeType
     * @return
     */
    public static boolean valideTradeType(String tradeType) {
        try {
            TradeType tt = TradeType.valueOf(tradeType);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
