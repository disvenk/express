package com.xxx.clients.controller;

import com.xxx.clients.service.SendAndReceiveAddressService;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.ExpReceiveAddress;
import com.xxx.model.business.ExpSendAddress;
import com.xxx.user.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("sendAndReceiveAdress")
public class SendAndReceiveAdressController {

    @Autowired
    private SendAndReceiveAddressService sendAndReceiveAddressService;

    /**
     * @Description: 查询默认寄件地址
     * @Author: disvenk.dai
     * @Date: 2018/1/12
     */
    @RequestMapping(value = "/findDefalutSendAddress",method = {RequestMethod.POST})
    public ResponseEntity findDefalutSendAddress(){
        ExpSendAddress expSendAddress = sendAndReceiveAddressService.get2(ExpSendAddress.class,"userId", CurrentUser.get().userId,"isDefault",true);
        return new ResponseEntity(new RestResponseEntity(100, "成功", expSendAddress), HttpStatus.OK);
    }

    /**
     * @Description: 查询默认收件地址
     * @Author: disvenk.dai
     * @Date: 2018/1/12
     */
    @RequestMapping(value = "/findDefalutReciveAddress",method = {RequestMethod.POST})
    public ResponseEntity findDefalutReciveAddress(){
        ExpReceiveAddress expReceiveAddress = sendAndReceiveAddressService.get2(ExpReceiveAddress.class,"userId", CurrentUser.get().userId,"isDefault",true);
        return new ResponseEntity(new RestResponseEntity(100, "成功", expReceiveAddress), HttpStatus.OK);
    }

}
