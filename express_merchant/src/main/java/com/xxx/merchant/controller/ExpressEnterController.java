package com.xxx.merchant.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.merchant.form.ExpressEnterForm;
import com.xxx.merchant.service.ExpressEnterService;
import com.xxx.model.business.ExpReceiveOrder;
import com.xxx.user.form.ExpressForm;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.date.SwitchUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;


@Controller
@RequestMapping("expressEnter")
public class ExpressEnterController {

    @Autowired
    private ExpressEnterService expressEnterService;

    /**
     * @Description: 快件扫描录入
     * @Author: disvenk
     * @Date: 2018/1/15
     */
    @RequestMapping(value = "/expressEnterCheck",method = {RequestMethod.POST})
    public ResponseEntity expressEnterCheck(@RequestBody ExpressEnterForm form){
        if(form.expressOrder==null || StringUtils.isBlank(form.expressOrder)){
        return new ResponseEntity(new RestResponseEntity(110,"订单不能为空",null), HttpStatus.OK);
        }
        ExpReceiveOrder expReceiveOrder = expressEnterService.expressEnterCheck(form.expressOrder, CurrentUser.get().schoolId);
        if(expReceiveOrder!=null){
            JSONObject json = new JSONObject();
            json.put("id",expReceiveOrder.getId());
            json.put("expressCompany",expReceiveOrder.getExpDeliveryCompany().getName());
            json.put("tel",expReceiveOrder.getReceiverTel());
            json.put("type",expReceiveOrder.getType());
            json.put("desc",expReceiveOrder.getGoodsDescription());
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",expReceiveOrder),HttpStatus.OK);
    }

    /**
     * @Description: 打印验证码
     * @Author: disvenk
     * @Date: 2018/1/15
     */
    @RequestMapping(value = "/createValidateCode",method = {RequestMethod.POST})
    public ResponseEntity createValidateCode(@RequestBody ExpressEnterForm form){
    return null;
    }

    /**
     * @Description: 快件查询
     * @Author: disvenk
     * @Date: 2018/1/15
     */
    @RequestMapping(value = "/checkExpress",method = {RequestMethod.POST})
    public ResponseEntity checkExpress(@RequestBody ExpressEnterForm form){
       List<ExpReceiveOrder> list = expressEnterService.checkExpress(CurrentUser.get().userId,
               CurrentUser.get().schoolId,
               form.expressOrder,
               form.tel);
        JSONArray data = new JSONArray();
       if(list!=null && !list.isEmpty()){
           for (ExpReceiveOrder expReceiveOrder1:list ) {
               JSONObject json = new JSONObject();
               json.put("id",expReceiveOrder1.getId());
               json.put("deliveryNo",expReceiveOrder1.getDeliveryNo());
               json.put("tel",expReceiveOrder1.getReceiverTel());
               json.put("status", SwitchUtils.orderStatus(expReceiveOrder1.getOrderStatus()));
               data.add(json);
           }
       }
       return new ResponseEntity(new RestResponseEntity(100,"成功",data),HttpStatus.OK);
    }


}
