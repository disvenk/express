package com.xxx.pay.controller;

import com.xxx.core.response.RestResponseEntity;
import com.xxx.pay.service.WeixinPayService;
import com.xxx.pay.form.UnifyOrderForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.SortedMap;

/**
 * 微信支付服务
 */
@RestController
@RequestMapping("/weixin_pay")
public class WeixinPayController {
    @Autowired
    private WeixinPayService weixinPayService;

    /**
     * 统一下单接口
     *
     * @param request
     * @param form
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/unify_order", method = {RequestMethod.POST})
    public ResponseEntity unifyOrder(HttpServletRequest request, @RequestBody UnifyOrderForm form) throws Exception {
        SortedMap<String, Object> sortedMap = weixinPayService.unifyOrder(form);
        return new ResponseEntity(new RestResponseEntity(100, "成功", sortedMap), HttpStatus.OK);
    }

    /**
     * 统一下单通知回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/unify_order_notify", method = {RequestMethod.POST})
    public
    @ResponseBody
    String unifyNotify(HttpServletRequest request) {

        return weixinPayService.unifyOrderNotify(request);
    }
}
