package com.xxx.pay.controller;

import com.xxx.core.response.RestResponseEntity;
import com.xxx.pay.Common;
import com.xxx.pay.form.UnifyOrderEntranceForm;
import com.xxx.pay.service.CashPayService;
import com.xxx.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 */
@RestController
@RequestMapping("/pay")
public class PayController {
    @Autowired
    private WeixinPayService weixinPayService;
//    @Autowired
//    private ZhifubaoPayService zhifubaoPayService;
//    @Autowired
//    private CashPayService cashPayService;

    /**
     * 支付统一入口
     *
     * @param request
     * @param form
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/union_entrance", method = {RequestMethod.POST})
    public ResponseEntity unionEntrance(HttpServletRequest request, @RequestBody UnifyOrderEntranceForm form) throws Exception {
        if (form.channel == null)
            return new ResponseEntity(new RestResponseEntity(110, "支付渠道为空", null), HttpStatus.OK);
        if (!Common.validatePayChannel(form.channel))
            return new ResponseEntity(new RestResponseEntity(110, "支付渠道错误", null), HttpStatus.OK);

        switch (form.channel) {
//            case "ALIPAY":  //支付宝
//                return new ResponseEntity(new RestResponseEntity(100, "成功", zhifubaoPayService.unifyOrder(form)), HttpStatus.OK);
            case "WECHAT":  //微信
                return new ResponseEntity(new RestResponseEntity(100, "成功", weixinPayService.unifyOrder(form)), HttpStatus.OK);
//            case "UPMP":    //银联
//                //noting to do!
//                break;
//           case "CASH":    //现金
//               return new ResponseEntity(new RestResponseEntity(100, "成功", cashPayService.cashTrade(form,form.channel)), HttpStatus.OK);
//
        }
        return null;
    }
}
