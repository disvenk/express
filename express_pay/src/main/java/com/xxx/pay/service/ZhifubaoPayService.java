package com.xxx.pay.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.service.CommonService;
import com.xxx.pay.Common;
import com.xxx.pay.form.UnifyOrderForm;
import com.xxx.pay.form.UnifyOrderNotifyForm;
import com.xxx.pay.service.PaymentService;
import com.xxx.pay.zhifubao.config.Configs;
import com.xxx.pay.zhifubao.model.TradeStatus;
import com.xxx.pay.zhifubao.model.builder.AlipayTradePrecreateRequestBuilder;
import com.xxx.pay.zhifubao.model.result.AlipayF2FPrecreateResult;
import com.xxx.pay.zhifubao.service.AlipayMonitorService;
import com.xxx.pay.zhifubao.service.AlipayTradeService;
import com.xxx.pay.zhifubao.service.impl.AlipayMonitorServiceImpl;
import com.xxx.pay.zhifubao.service.impl.AlipayTradeServiceImpl;
import com.xxx.pay.zhifubao.service.impl.AlipayTradeWithHBServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 */
@Service
public class ZhifubaoPayService extends CommonService {
    @Autowired
    private PaymentService paymentService;

    //PC网站支付
    private static AlipayClient alipayClient;
    // 支付宝当面付2.0服务
    private static AlipayTradeService tradeService;
    // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
    private static AlipayTradeService   tradeWithHBService;
    // 支付宝交易保障接口服务，供测试接口api使用，请先阅读readme.txt
    private static AlipayMonitorService monitorService;

    static {
        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
        tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();

        /** 如果需要在程序中覆盖Configs提供的默认参数, 可以使用ClientBuilder类的setXXX方法修改默认参数 否则使用代码中的默认设置 */
        monitorService = new AlipayMonitorServiceImpl.ClientBuilder().build();
        monitorService = new AlipayMonitorServiceImpl.ClientBuilder()
                .setGatewayUrl("http://mcloudmonitor.com/gateway.do").setCharset("GBK")
                .setFormat("json").build();

        alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", Configs.getAppid(), Configs.getPrivateKey(), "json", "UTF-8", Configs.getAlipayPublicKey(), "RSA2");

   }

    /**
    * 统一收单线下交易预创建  当面付
    * @param form
     * @return
     * @throws Exception
     */
    public AlipayTradePrecreateResponse unifyOrder(UnifyOrderForm form) throws Exception {
        if (form.orderId == null)
            throw new ResponseEntityException(110, "订单ID不能为空");
        if (form.orderType == null)
            throw new ResponseEntityException(120, "订单类型不能为空");
        if (!Common.validateOrderType(form.orderType))
            throw new ResponseEntityException(130, "订单类型不正确");
        if (form.totalFee <= 0)
            throw new ResponseEntityException(140, "订单总金额不正确");
        if (StringUtils.isBlank(form.subject))
           throw new ResponseEntityException(150, "订单标题不能为空");
        if (StringUtils.isBlank(form.body))
            throw new ResponseEntityException(160, "商品描述为空");

        paymentService.createPayment(form, "ALIPAY", 0);

        //开放平台SDK封装了签名实现，只需在创建DefaultAlipayClient对象时，设置请求网关(gateway)，应用id(app_id)，应用私钥(private_key)，编码格式(charset)，支付宝公钥(alipay_public_key)，签名类型(sign_type)即可，报文请求时会自动进行签名
//        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", Configs.getAppid(), Configs.getPrivateKey(), "json", "GBK", Configs.getAlipayPublicKey(), "RSA2");

       //创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder();
        builder.setOutTradeNo(form.outTradeNo);
        builder.setTotalAmount(String.valueOf(form.totalFee * 1d / 100));
        builder.setSubject(form.subject);   //订单标题
        builder.setBody(form.body);
        builder.setNotifyUrl(Configs.getNotifyUrl());  //支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        if (result.getTradeStatus().equals(TradeStatus.SUCCESS)) {
            return result.getResponse();
        } else {
            throw new ResponseEntityException(170, "预下单失败");
        }
    }


    /**
     * 统一收单线下交易预创建   PC网站支付
     * @param form
     * @return
    * @throws Exception
     */
    public void unifyOrder(HttpServletResponse response, UnifyOrderForm form) throws Exception {
        if (form.orderId == null)
            throw new ResponseEntityException(110, "订单ID不能为空");
        if (form.orderType == null)
            throw new ResponseEntityException(120, "订单类型不能为空");
        if (!Common.validateOrderType(form.orderType))
            throw new ResponseEntityException(130, "订单类型不正确");
        if (form.totalFee <= 0)
            throw new ResponseEntityException(140, "订单总金额不正确");
        if (StringUtils.isBlank(form.subject))
           throw new ResponseEntityException(150, "订单标题不能为空");
        if (StringUtils.isBlank(form.body))
            throw new ResponseEntityException(160, "商品描述为空");

       paymentService.createPayment(form, "ALIPAY", 0);

       AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
//        request.setBizModel(model);
        request.setNotifyUrl(Configs.getNotifyUrl());
        request.setReturnUrl(form.returnUrl);
        String content = "{" +
                "    \"out_trade_no\":\"" + form.outTradeNo + "\"," +
                "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
                "    \"total_amount\":" + form.totalFee * 1d/100 + "," +
                "    \"subject\":\"" + "\"合一智造-订单支付\"" + "\"," +
                "    \"body\":\"" + "\"合一智造-订单支付\"" + "\"," +
                "    \"charset\":\"UTF-8\"" +
                "  }";
        request.setBizContent(content);//填充业务参数
        String formBody  = alipayClient.pageExecute(request).getBody();//调用SDK生成表单
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(formBody);//直接将完整的表单html输出到页面
        response.getWriter().flush();
        response.getWriter().close();
    }

    /**
     * 当面付异步通知-仅用于扫码支付
     * 提示：程序执行完后必须打印输出“success”（不包含引号）。如果商户反馈给支付宝的字符不是success这7个字符，
     * 支付宝服务器会不断重发通知，直到超过24小时22分钟。一般情况下，25小时以内完成8次通知（通知的间隔频率一般是：
     * 4m,10m,10m,1h,2h,6h,15h）；
     *
     * @param form
     * @return
     */
    @CacheEvict(value = {"Deal,PackOrder,RechargeOrder,SecUserCard,SecUserCardItem,StoreProductOrder,RechargeUserRecord"}, allEntries = true)
    public void unifyOrderNotify(HttpServletResponse response,UnifyOrderNotifyForm form) {
        //验证签名，略！
        PrintWriter out = null;
        try {
            paymentService.FinishPayment(form.getOut_trade_no());
            out = response.getWriter();
            out.flush();
            out.print("success");
        } catch (Exception e) {
            e.printStackTrace();
            out.print("fail");
        } finally {
            if (out != null)
                out.close();
        }
    }

    public static void tradePage(HttpServletResponse httpResponse, AlipayTradePayModel model, String notifyUrl, String returnUrl) throws AlipayApiException, IOException{
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
//        request.setBizModel(model);
        request.setNotifyUrl(notifyUrl);
//        request.setReturnUrl(returnUrl);
       request.setBizContent("{" +
                "    \"out_trade_no\":\"2015032101212331\"," +
                "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
               "    \"total_amount\":0.01," +
                "    \"subject\":\"Iphone6 16G\"," +
                "    \"body\":\"Iphone6 16G\"" +
                "  }");//填充业务参数
        String form  = alipayClient.pageExecute(request).getBody();//调用SDK生成表单
        httpResponse.setContentType("text/html;charset=GBK");
        httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }

}

