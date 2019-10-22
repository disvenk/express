package com.xxx.clients.controller;

import com.xxx.clients.form.IdForm;
import com.xxx.clients.form.TranDoorForm;
import com.xxx.clients.service.ReceiveOrderService;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.ExpReceiveOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Description:转派件上门接口
 * @Author: disvenk.dai
 * @Date: 下午 1:44 2018/1/18 0018
 */
@Controller
@RequestMapping("transDoor")
public class PayReceiveOrderController {

    @Autowired
    private ReceiveOrderService receiveOrderService;


    /**
     * @Description: 转派件上门(pass)
     * @Author: disvenk.dai
     * @Date: 2018/1/18
     */
    @RequestMapping(value = "/transDoorStatus",method = {RequestMethod.POST})
    public ResponseEntity transDoor(@RequestBody TranDoorForm form) throws UpsertException, ResponseEntityException {
        if(form.id==null){
            return new ResponseEntity(new RestResponseEntity(110,"id不能为空",null), HttpStatus.OK);
        }
        if(form.addressId==null){
            return new ResponseEntity(new RestResponseEntity(110,"地址id不能为空",null), HttpStatus.OK);
        }
        ExpReceiveOrder expReceiveOrder = receiveOrderService.transDoor(form.id,form.doorTime,form.fee,form.addressId,form.remarks);
        return new ResponseEntity(new RestResponseEntity(100,"成功",expReceiveOrder),HttpStatus.OK);
    }
}
