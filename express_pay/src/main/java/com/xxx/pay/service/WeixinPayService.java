package com.xxx.pay.service;

import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.Payment;
import com.xxx.pay.Common;
import com.xxx.pay.weixin.UnifyOrder;
import com.xxx.pay.form.UnifyOrderForm;
import com.xxx.pay.weixin.WeixinUtil;
import com.xxx.pay.weixin.config.Configs;
import com.xxx.utils.HttpClient;
import com.xxx.utils.StreamUtil;
import com.xxx.utils.XmlUtil;
import com.xxx.utils.contract.SimpleResult;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.StringReader;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 */
@Service
public class WeixinPayService extends CommonService {
    @Autowired
    private PaymentService paymentService;

    /**
     * 统一下单（无需设置支付授权目录）
     *
     * @param form
     * @return
     * @throws Exception
     */
    public SortedMap<String, Object> unifyOrder(UnifyOrderForm form) throws Exception {
        if (form.orderId == null)
            throw new ResponseEntityException(110, "订单ID不能为空");
        if (form.orderType == null)
            throw new ResponseEntityException(120, "订单类型不能为空");
        if (!Common.validateOrderType(form.orderType))
            throw new ResponseEntityException(130, "订单类型不正确");
        if (form.totalFee <= 0)
            throw new ResponseEntityException(140, "订单总金额不正确");
        if (StringUtils.isBlank(form.tradeType))
            throw new ResponseEntityException(150, "交易类型不能为空");
        if (!UnifyOrder.valideTradeType(form.tradeType))
            throw new ResponseEntityException(160, "交易类型格式不正确");
        if (StringUtils.isBlank(form.body))
            throw new ResponseEntityException(170, "商品描述为空");
        if ("NATIVE".equals(form.tradeType) && StringUtils.isBlank(form.productId))
            throw new ResponseEntityException(180, "扫码支付时必须提productid");
//        form.openid = "oAp_D1Qp_coNSax4zZ60KhnJK8iQ";
        if ("JSAPI".equals(form.tradeType) && StringUtils.isBlank(form.openid))
            throw new ResponseEntityException(190, "公众号支付时必须提供openid");

        System.out.println("-------- 进入统一下单： " + form.toString() + " ----------");

        Payment payment = paymentService.createPayment(form, "WECHAT", 0);

        System.out.println("--------- 下单校验完成 ： " + form.toString() + "  -------");

        UnifyOrder unifyOrder = new UnifyOrder(form.outTradeNo, form.totalFee, form.tradeType, form.productId, form.openid, form.body, "");
        String xmlStr = XmlUtil.map2xmlBody(unifyOrder.buildParams(), "xml");
        //String xmlStr=  FileUtils.readFileToString(new File(this.getClass().getResource("/weixin/unifyOrderTemplate.xml").toURI()),"utf-8");
        HttpClient client = new HttpClient();
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        SimpleResult result = client.requestXml(HttpClient.Method.POST, url, xmlStr, null, null, null, null);
        if (StringUtils.isBlank(result.getMessage()))
            if (payment != null) {
                delete(payment);
                throw new ResponseEntityException(200, "统一下单失败");
            }

        SAXReader saxreader = new SAXReader();
        Document doc = saxreader.read(new StringReader(result.getMessage()));
        String returnCode = doc.selectSingleNode("/xml/return_code").getStringValue();
        String returnMsg = doc.selectSingleNode("/xml/return_msg").getStringValue();
        if (returnCode.equals("SUCCESS")) {  //此字段是通信标识，非交易标识
            String resultCode = doc.selectSingleNode("/xml/result_code").getStringValue();
            String appid = doc.selectSingleNode("/xml/appid").getStringValue();
            String mchId = doc.selectSingleNode("/xml/mch_id").getStringValue();
            String deviceInfo = doc.selectSingleNode("/xml/device_info") != null ? doc.selectSingleNode("/xml/device_info").getStringValue() : "";  //可能为空
            String nonceStr = doc.selectSingleNode("/xml/nonce_str").getStringValue();
            String sign = doc.selectSingleNode("/xml/sign").getStringValue();
            String errCode = doc.selectSingleNode("/xml/err_code") != null ? doc.selectSingleNode("/xml/err_code").getStringValue() : "";  //详细参见下文错误列表
            String errCodeDes = doc.selectSingleNode("/xml/err_code_des") != null ? doc.selectSingleNode("/xml/err_code_des").getStringValue() : "";  //如：商户订单号重复
            if (resultCode.equals("SUCCESS")) {  //交易标识
                String tradeType = doc.selectSingleNode("/xml/trade_type").getStringValue();  //交易类型，取值为：JSAPI，NATIVE，APP等
                String prepayId = doc.selectSingleNode("/xml/prepay_id").getStringValue();    //微信生成的预支付回话标识，用于后续接口调用中使用，该值有效期为2小时
                String codeUrl = doc.selectSingleNode("/xml/code_url") == null ? "" :
                        doc.selectSingleNode("/xml/code_url").getStringValue();      //rade_type为NATIVE时有返回，用于生成二维码，展示给用户进行扫码支付

                SortedMap<String, Object> param = new TreeMap<>();
                param.put("appId", appid);
                param.put("nonceStr", nonceStr);
                param.put("package", "prepay_id=" + prepayId);
                param.put("timeStamp", (System.currentTimeMillis() / 1000) + "");
                param.put("signType", "MD5");
                param.put("paySign", WeixinUtil.createPaySign(param, Configs.key));
                param.put("tradeType", tradeType);
                param.put("codeUrl", codeUrl);
                return param;
            } else {
                throw new ResponseEntityException(210, errCodeDes);
            }
        } else {
            throw new ResponseEntityException(220, "统一下单通信标识异常：" + returnMsg);
        }
    }

    /**
     * 统一下单通知回调
     *
     * @param request
     * @return
     */
    @CacheEvict(value = {"Deal,PackOrder,RechargeOrder,SecUserCard,SecUserCardItem,ExpReceiveOrder,ExpSendOrder,RechargeUserRecord"}, allEntries = true)
    public String unifyOrderNotify(HttpServletRequest request) {
        System.out.println("---------- 进入微信支付回调 --------------");
        Map<String, Object> rtn = new TreeMap<String, Object>();
        rtn.put("return_code", "SUCCESS");   //SUCCESS 或 FAIL
        //rtn.put("return_msg", "OK");
        try {
            String xmlStr = StreamUtil.readFromInputStream(request.getInputStream()).toString();
            SAXReader saxreader = new SAXReader();
            Document doc = saxreader.read(new StringReader(xmlStr));
            String returnCode = doc.selectSingleNode("/xml/return_code").getStringValue();
            if (returnCode.equals("SUCCESS")) {
//                String appid = doc.selectSingleNode("/xml/appid").getStringValue();
                String out_trade_no = doc.selectSingleNode("/xml/out_trade_no").getStringValue();
//                String bank_type = doc.selectSingleNode("/xml/bank_type").getStringValue();  //CFT
//                int cash_fee = Integer.parseInt(doc.selectSingleNode("/xml/cash_fee").getStringValue());
//                String fee_type = doc.selectSingleNode("/xml/fee_type").getStringValue();
//                String is_subscribe = doc.selectSingleNode("/xml/is_subscribe").getStringValue();
//                String mch_id = doc.selectSingleNode("/xml/mch_id").getStringValue();
//                String nonce_str = doc.selectSingleNode("/xml/nonce_str").getStringValue();
//                String payOpenid = doc.selectSingleNode("/xml/openid").getStringValue();  //注意：这个openid是具有微信支付功能的公众号的openid。
//                String sign = doc.selectSingleNode("/xml/sign").getStringValue();
//                String time_end = doc.selectSingleNode("/xml/time_end").getStringValue();
//                int total_fee = Integer.parseInt(doc.selectSingleNode("/xml/total_fee").getStringValue());
//                String trade_type = doc.selectSingleNode("/xml/trade_type").getStringValue();
//                String transaction_id = doc.selectSingleNode("/xml/transaction_id").getStringValue();
//                String attach = doc.selectSingleNode("/xml/attach") != null ? doc.selectSingleNode("/xml/attach").getStringValue() : "";  //商家数据包，原样返回
                paymentService.FinishPayment(out_trade_no);
            }
        } catch (Throwable e) {
            rtn.put("return_code", "FAIL");
            e.printStackTrace();
        }

        String returnXml = XmlUtil.map2xmlBody(rtn, "xml");
        return returnXml;
    }
}

