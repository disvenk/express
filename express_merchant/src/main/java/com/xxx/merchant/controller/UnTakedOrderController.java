package com.xxx.merchant.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.merchant.form.ExpressEnterForm;
import com.xxx.merchant.form.IdForm;
import com.xxx.merchant.service.UnTakeOrderService;
import com.xxx.model.business.ExpReceiveOrder;
import com.xxx.model.business.ExpSchool;
import com.xxx.model.business.ExpUser;
import com.xxx.user.Commo;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
import com.xxx.utils.date.DateTimeUtil;
import org.omg.CORBA.ORB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sun.nio.cs.ext.SJIS;

import java.util.List;

/**
 * @Description:
 * @Author: disvenk.dai
 * @Date: 下午 1:53 2018/1/15 0015
 */
@Controller
@RequestMapping("orderTake")
public class UnTakedOrderController {

    @Autowired
    private UnTakeOrderService unTakeOrderService;

    /**
     * @Description: 待接单列表和接单信息列表(pass)
     * @Author: disvenk
     * @Date: 2018/1/15
     */
    @RequestMapping(value = "/orderTakeList",method = {RequestMethod.POST})
    public ResponseEntity orderTakeList(@RequestBody IdForm form){
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<ExpReceiveOrder> list = unTakeOrderService.orderTakeList(pageQuery,form);
        JSONArray data = new JSONArray();

        if(list!= null && !list.isEmpty()){
            for (ExpReceiveOrder expReceiveOrder:list ) {
                JSONObject json = new JSONObject();
                json.put("id",expReceiveOrder.getId());
                json.put("orderNo",expReceiveOrder.getOrderNo());
                json.put("deliveryNo",expReceiveOrder.getDeliveryNo());
                json.put("expressCompany",expReceiveOrder.getExpDeliveryCompany().getName());
                ExpUser createBy = unTakeOrderService.get2(ExpUser.class,expReceiveOrder.getUserId());
                json.put("createByTel",createBy.getUserMobile());
                json.put("orderStatus",Commo.parseReceiveOrderStatus(expReceiveOrder.getOrderStatus()));
                json.put("address",expReceiveOrder.getReceiverAddress());
                json.put("doorTime",expReceiveOrder.getBeginEndTime());
                json.put("createdDate",DateTimeUtils.parseStr(expReceiveOrder.getCreatedDate()));
                json.put("fee",expReceiveOrder.getTodoorFee());
                data.add(json);
            }

        }
        return new ResponseEntity(new PageResponseEntity(100, "成功",data, pageQuery.page, pageQuery.limit, list.total), HttpStatus.OK);
    }

    /**
     * @Description: 待接单详情(pass)
     * @Author: disvenk
     * @Date: 2018/1/15
     */
    @RequestMapping(value = "/orderTakingDetail",method = {RequestMethod.POST})
    public ResponseEntity orderTakingDetail(@RequestBody ExpressEnterForm form) throws ResponseEntityException {
        if(form.id==null){
            return new ResponseEntity(new RestResponseEntity(110,"id不能为空",null),HttpStatus.OK);
        }

        ExpReceiveOrder expReceiveOrder = unTakeOrderService.orderTakingDetail(form.id);
        JSONObject json = new JSONObject();
        json.put("id",expReceiveOrder.getId());
        json.put("orderNo",expReceiveOrder.getOrderNo());
        json.put("deliveryNo",expReceiveOrder.getDeliveryNo());
        json.put("delivery",expReceiveOrder.getExpDeliveryCompany().getName());
        json.put("recieverName",expReceiveOrder.getReceiverName());
        json.put("recieverTel",expReceiveOrder.getReceiverTel());

        StringBuilder recieAddress = new StringBuilder();
        recieAddress.append( expReceiveOrder.getProvince());
        recieAddress.append(expReceiveOrder.getCity()==null?"":expReceiveOrder.getCity());
        recieAddress.append(expReceiveOrder.getZone()==null?"":expReceiveOrder.getZone());
        recieAddress.append(expReceiveOrder.getReceiverAddress()==null?"":expReceiveOrder.getReceiverAddress());
        json.put("address",recieAddress.toString());

        json.put("doorTimeBeginEnd",expReceiveOrder.getBeginEndTime());
        json.put("orderStatus", Commo.parseReceiveOrderStatus(expReceiveOrder.getOrderStatus()));
        json.put("fee",expReceiveOrder.getTodoorFee());
        if(expReceiveOrder.getCapture()!=null){
            json.put("img", OSSClientUtil.getObjectUrl(expReceiveOrder.getCapture()));
        }
        json.put("remarks",expReceiveOrder.getRemarks()==null?"":expReceiveOrder.getRemarks());
        ExpSchool exp = unTakeOrderService.get2(ExpSchool.class,"id",CurrentUser.get().schoolId,"logicDeleted",false);
        json.put("capture",exp.getCapture());//false否,true是
        return new ResponseEntity(new RestResponseEntity(100,"成功",json),HttpStatus.OK);
    }

    /**
     * @Description: 确认接单(pass)
     * @Author: disvenk
     * @Date: 2018/1/16
     */
    @RequestMapping(value = "/sureTaked",method = {RequestMethod.POST})
    public ResponseEntity sureTaked(@RequestBody ExpressEnterForm form) throws ResponseEntityException, UpsertException {
        if(form.id==null){
            return new ResponseEntity(new RestResponseEntity(110,"id不能为空",null),HttpStatus.OK);
        }
        unTakeOrderService.sureTaked(CurrentUser.get().schoolId,form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null),HttpStatus.OK);
    }

    /**
     * @Description: 确认签收(pass)
     * @Author: disvenk
     * @Date: 2018/1/16
     */
    @RequestMapping(value = "sureSign",method = {RequestMethod.POST})
    public ResponseEntity sureSign(@RequestBody ExpressEnterForm form) throws UpsertException, ResponseEntityException {
        if(form.id==null){
            return  new ResponseEntity(new RestResponseEntity(110,"id不能为空",null),HttpStatus.OK);
        }
        unTakeOrderService.sureSign(form.id,form.base64);

    return new ResponseEntity(new RestResponseEntity(100,"成功",null),HttpStatus.OK);
    }
}
