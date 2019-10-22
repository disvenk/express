package com.xxx.merchant.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.merchant.form.ExpressEnterForm;
import com.xxx.merchant.service.SelfTakeExpressService;
import com.xxx.model.business.ExpReceiveOrder;
import com.xxx.model.business.ExpSchool;
import com.xxx.model.business.ExpUser;
import com.xxx.user.Commo;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.json.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @Description:自行取件
 * @Author: disvenk.dai
 * @Date: 下午 4:18 2018/1/15 0015
 */
@Controller
@RequestMapping("selfTake")
public class SelfTakeExpressController {

    @Autowired
    private SelfTakeExpressService selfTakeExpressService;

    /**
     * @Description: 自行取件列表
     * @Author: disvenk
     * @Date: 2018/1/15
     */
   /* @RequestMapping(value = "/selfTakeList",method = {RequestMethod.POST})
    public ResponseEntity selfTakeList(){
        List<ExpReceiveOrder> list = selfTakeExpressService.selfTakeList();
        JSONArray data = new JSONArray();
        if(list!=null && !list.isEmpty()){
            for (ExpReceiveOrder expReceiveOrder: list) {
                JSONObject json = new JSONObject();
                json.put("id",expReceiveOrder.getId());
                json.put("orderNo",expReceiveOrder.getOrderNo());
                json.put("deliveryCompany",expReceiveOrder.getExpDeliveryCompany().getName());
                json.put("deliveryNo",expReceiveOrder.getDeliveryNo());
                json.put("createdDate", DateTimeUtils.parseStr(expReceiveOrder.getCreatedDate()));
            }
        }
       return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }*/

    /**
     * @Description: 快件签收模块查询
     * @Author: disvenk
     * @Date: 2018/1/15
     */
    @RequestMapping(value = "/selfTakeSingle",method = {RequestMethod.POST})
    public ResponseEntity selfTakeSingle(@RequestBody ExpressEnterForm form) throws ResponseEntityException {
        if(form.deliveryNo==null || StringUtils.isBlank(form.deliveryNo)){
            return new ResponseEntity(new RestResponseEntity(110,"快递单号不能为空",null),HttpStatus.OK);
        }
        ExpReceiveOrder expReceiveOrder = selfTakeExpressService.selfTakeSign(form.deliveryNo);
        JSONObject json = new JSONObject();
        json.put("id",expReceiveOrder.getId());
        json.put("deliveryNo",expReceiveOrder.getDeliveryNo());
        json.put("deliveryCompany",expReceiveOrder.getExpDeliveryCompany().getName());
        json.put("orderNo",expReceiveOrder.getOrderNo());
        json.put("doorTime",expReceiveOrder.getBeginEndTime());
        ExpUser expUser = selfTakeExpressService.get2(ExpUser.class,"id",expReceiveOrder.getUserId());
        json.put("receiveTel",expReceiveOrder.getReceiverTel()==null?"":expReceiveOrder.getReceiverTel());//收件人
        json.put("userMobile",expUser.getUserMobile());//用户手机号
        json.put("orderStatus", Commo.parseReceiveOrderStatus(expReceiveOrder.getOrderStatus()));
        if(expReceiveOrder.getCapture()!=null){
            json.put("img", OSSClientUtil.getObjectUrl(expReceiveOrder.getCapture()));
        }
        json.put("validateCode",expReceiveOrder.getValidateCode()==null?"":expReceiveOrder.getValidateCode());
        json.put("createdDate",DateTimeUtils.parseStr(expReceiveOrder.getCreatedDate()));
        json.put("arrivedDate",DateTimeUtils.parseStr(expReceiveOrder.getArrivedDate()));
        ExpSchool exp = selfTakeExpressService.get2(ExpSchool.class,"id",CurrentUser.get().schoolId,"logicDeleted",false);
        json.put("capture",exp==null?"":exp.getCapture());//0否
        return new ResponseEntity(new RestResponseEntity(100,"成功",json),HttpStatus.OK);
    }

    /**
     * @Description: 快件签收模块确认签收
     * @Author: disvenk
     * @Date: 2018/1/15
     */
    @RequestMapping(value = "/sureSign",method = {RequestMethod.POST})
    public ResponseEntity sureSign(@RequestBody ExpressEnterForm form) throws ResponseEntityException, UpsertException {
    if(form.id==null){
        return new ResponseEntity(new RestResponseEntity(110,"id不能为空",null),HttpStatus.OK);
    }
    selfTakeExpressService.sureSign(form.id,form.base64, CurrentUser.get().userId,CurrentUser.get().schoolId);
    return new ResponseEntity(new RestResponseEntity(100,"成功",null),HttpStatus.OK);
    }
}
