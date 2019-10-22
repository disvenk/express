package com.xxx.pay.controller;

import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.pay.form.UnifyOrderForm;
import com.xxx.pay.form.UnifyOrderNotifyForm;
import com.xxx.pay.service.ZhifubaoPayService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 支付宝支付服务
 */
@RestController
@RequestMapping("/zhifubao_pay")
public class ZhifubaoPayController {
    @Autowired
    private ZhifubaoPayService zhifubaoPayService;

    /**
     * 统一下单接口  当面付
     *
     * @param request
     * @param form
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/unify_order", method = {RequestMethod.POST})
    public ResponseEntity unifyOrder(HttpServletRequest request, @RequestBody UnifyOrderForm form) throws Exception {
        AlipayTradePrecreateResponse response = zhifubaoPayService.unifyOrder(form);
        return new ResponseEntity(new RestResponseEntity(100, "成功", response), HttpStatus.OK);
    }

    /**
     * 统一下单接口  PC网站支付  跳转至支付宝支付页面
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/unify_order", method = {RequestMethod.GET})
    public void unifyOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String orderId = request.getParameter("orderId");
        String orderType = request.getParameter("orderType");
        String totalFee = request.getParameter("totalFee");
        String subject = request.getParameter("subject");
        String body = request.getParameter("body");
        String outTradeNo = request.getParameter("outTradeNo");
        String returnUrl = request.getParameter("returnUrl");
        UnifyOrderForm form = new UnifyOrderForm();
        form.orderId = StringUtils.isBlank(orderId) ? null : Integer.parseInt(orderId);
        form.orderType = StringUtils.isBlank(orderType) ? null : Integer.parseInt(orderType);
        form.totalFee = StringUtils.isBlank(totalFee) ? null : Integer.parseInt(totalFee);
        form.subject = subject;
        form.body = body;
        form.outTradeNo = outTradeNo;
        form.returnUrl = returnUrl;
        zhifubaoPayService.unifyOrder(response, form);
    }

    /**
     * 统一下单通知回调
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/unify_order_notify", method = {RequestMethod.POST})
    public void unifyNotify(HttpServletRequest request, HttpServletResponse response, UnifyOrderNotifyForm form) {
        zhifubaoPayService.unifyOrderNotify(response, form);
    }

    @RequestMapping(value = "/aa")
    public void aa(HttpServletRequest request, HttpServletResponse response) {
        try {
            String totalAmount = "0.01";
            String outTradeNo = (System.currentTimeMillis() / 1000) + "";
//            log.info("pc outTradeNo>"+outTradeNo);

            String returnUrl = "http://kaasales.free.ngrok.cc/kaa_sales/zhifubao_pay/unify_order_notify";
            String notifyUrl = "http://kaasales.free.ngrok.cc/kaa_sales/zhifubao_pay/unify_order_notify";
            AlipayTradePayModel model = new AlipayTradePayModel();

            model.setOutTradeNo(outTradeNo);
//            model.setProductCode("FAST_INSTANT_TRADE_PAY");
            model.setTotalAmount(totalAmount);
            model.setSubject("Javen PC支付测试");
            model.setBody("Javen IJPay PC支付测试");

            ZhifubaoPayService.tradePage(response, model, notifyUrl, returnUrl);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
