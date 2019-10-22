package com.xxx.merchant.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.merchant.form.ExpressEnterForm;
import com.xxx.merchant.service.TakeExpressDoorService;
import com.xxx.model.business.ExpReceiveOrder;
import com.xxx.model.business.ExpSchool;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @Description:派件上门
 * @Author: disvenk.dai
 * @Date: 下午 4:04 2018/1/16 0016
 */
@Controller
@RequestMapping("tekeToDoor")
public class TakeExpressDoorController {

    @Autowired
    private TakeExpressDoorService takeExpressDoorService;

    /**
     * @Description: 上门派件列表
     * @Author: disvenk.dai
     * @Date: 2018/1/16
     */
    @RequestMapping(value = "takeToDoorList",method = {RequestMethod.POST})
    public ResponseEntity takeToDoorList(){
       List<ExpReceiveOrder> expReceiveOrderList = takeExpressDoorService.takeToDoorList(CurrentUser.get().userId,CurrentUser.get().schoolId);
        JSONArray data = new JSONArray();
        if(expReceiveOrderList!=null && !expReceiveOrderList.isEmpty()){
            for (ExpReceiveOrder expReceiveOrder: expReceiveOrderList) {
                JSONObject json = new JSONObject();
                json.put("id",expReceiveOrder.getId());
                json.put("orderNo",expReceiveOrder.getOrderNo());
                json.put("deliveryCompany",expReceiveOrder.getExpDeliveryCompany().getName());
                json.put("deliveryNo",expReceiveOrder.getDeliveryNo());
                json.put("createdDate", DateTimeUtils.parseStr(expReceiveOrder.getCreatedDate()));
            }
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }

    /**
     * @Description: 上门派件确认签收详情
     * @Author: disvenk
     * @Date: 2018/1/15
     */
    @RequestMapping(value = "/selfTakeSingle",method = {RequestMethod.POST})
    public ResponseEntity selfTakeSingle(@RequestBody ExpressEnterForm form) throws ResponseEntityException {
        if(form.id==null){
            return new ResponseEntity(new RestResponseEntity(110,"id不能为空",null),HttpStatus.OK);
        }
        ExpReceiveOrder expReceiveOrder = takeExpressDoorService.selfTakeSingle(form.id);
        JSONObject json = new JSONObject();
        json.put("id",expReceiveOrder.getId());
        json.put("deliveryNo",expReceiveOrder.getDeliveryNo());
        json.put("deliveryCompany",expReceiveOrder.getExpDeliveryCompany().getName());
        json.put("orderNo",expReceiveOrder.getOrderNo());
        json.put("doorTime",expReceiveOrder.getBeginEndTime());
        json.put("tel",expReceiveOrder.getReceiverTel());
        json.put("validateCode",expReceiveOrder.getValidateCode()==null?"":expReceiveOrder.getValidateCode());
        json.put("createdDate",DateTimeUtils.parseStr(expReceiveOrder.getCreatedDate()));
        json.put("arrivedDate",DateTimeUtils.parseStr(expReceiveOrder.getArrivedDate()));
        ExpSchool exp = takeExpressDoorService.get2(ExpSchool.class,"id",CurrentUser.get().schoolId,"logicDeleted",false);
        json.put("capture",exp.getCapture());//false否,true是
        return new ResponseEntity(new RestResponseEntity(100,"成功",json),HttpStatus.OK);
    }

    /**
     * @Description: 上门派件确认签收
     * @Author: disvenk
     * @Date: 2018/1/15
     */
    @RequestMapping(value = "/sureSign",method = {RequestMethod.POST})
    public ResponseEntity sureSign(@RequestBody ExpressEnterForm form) throws ResponseEntityException, UpsertException {
        if(form.id==null){
            return new ResponseEntity(new RestResponseEntity(110,"id不能为空",null),HttpStatus.OK);
        }
        takeExpressDoorService.sureSign(form.id,form.base64, CurrentUser.get().userId,CurrentUser.get().schoolId);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null),HttpStatus.OK);
    }
}
