//package com.shiyu.pay.controller;
//
//import com.shiyu.core.response.RestResponseEntity;
//import com.shiyu.pay.Common;
//import com.shiyu.pay.form.PayForm;
//import com.shiyu.pay.service.CashPayService;
//import com.shiyu.pay.service.WeixinPayService;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//
///**
// * Created by wanghua on 17/3/17.
// * 现金支付服务
// */
//@RestController
//@RequestMapping("/cash_pay")
//public class CashPayController {
//    @Autowired
//    private CashPayService cashPayService;
//
//    @RequestMapping(value = "/trade", method = {RequestMethod.POST})
//    public ResponseEntity trade(HttpServletRequest request, @RequestBody PayForm payForm) throws Exception {
//        cashPayService.cashTrade(payForm,"CASH");
//        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
//    }
//}
