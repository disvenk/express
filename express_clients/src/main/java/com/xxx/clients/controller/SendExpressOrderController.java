package com.xxx.clients.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.clients.form.IdForm;
import com.xxx.clients.form.SendExpressForm;
import com.xxx.clients.service.SaveSendExpressOrderService;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.*;
import com.xxx.user.Commo;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("sendExpressOrder")
public class SendExpressOrderController {

    @Autowired
    private SaveSendExpressOrderService saveSendExpressOrderService;

    /**
     * @Description: 寄件
     * @Author: disvenk.dai
     * @Date: 2018/1/12
     */
    @RequestMapping(value = "/saveExpressOrder",method = {RequestMethod.POST})
    public ResponseEntity saveSendExpressOrder(@RequestBody SendExpressForm form){

        if(form.sendPid==null)return new ResponseEntity(new RestResponseEntity(110, "寄件人id不能为空", null), HttpStatus.OK);
        if(form.recievePid==null)return new ResponseEntity(new RestResponseEntity(110, "收件人id不能为空", null), HttpStatus.OK);
        if(form.goodsTypeId==null)return new ResponseEntity(new RestResponseEntity(110, "物品id不能为空", null), HttpStatus.OK);
        if(form.sendType==1){//0
            if(form.doorTime==null || StringUtils.isBlank(form.doorTime))return new ResponseEntity(new RestResponseEntity(110, "上门时间不能为空", null), HttpStatus.OK);
        }
        if(form.expressCompanyId==null)return new ResponseEntity(new RestResponseEntity(110, "快递公司id不能为空", null), HttpStatus.OK);

        if(form.sendType==null)return new ResponseEntity(new RestResponseEntity(110, "寄件类型不能为空", null), HttpStatus.OK);

       ExpSendOrder sendOrder = saveSendExpressOrderService.saveSendExpressOrder(form);
        return new ResponseEntity(new RestResponseEntity(100, "成功", sendOrder.getId()), HttpStatus.OK);
    }

    /**
     * @Description: 寄件订单列表
     * @Author: disvenk.dai
     * @Date: 2018/1/13
     */
    @RequestMapping(value = "/findSendOrderList", method = {RequestMethod.POST})
    public ResponseEntity findSendOrderList(@RequestBody IdForm form) throws Exception {
        List<ExpSendOrder> list = saveSendExpressOrderService.findSendOrderList(CurrentUser.get().userId, CurrentUser.get().schoolId, form.orderType);
        JSONArray data = new JSONArray();
        for (ExpSendOrder order : list) {
            JSONObject json = new JSONObject();
            json.put("id", order.getId());
            json.put("orderNo", order.getOrderNo());
            json.put("deliveryNo", order.getDeliveryNo());
            json.put("deliveryCompanyId", order.getDeliveryId());
            json.put("deliveryCompanyName", order.getExpDeliveryCompany() == null ? "" : order.getExpDeliveryCompany().getName());
            json.put("orderDate",order.getCreatedDate()==null? "": DateTimeUtils.parseStr(order.getCreatedDate()));
            json.put("orderStatus", order.getOrderStatus());
            json.put("orderStatusName", Commo.parseOrderStatus(order.getOrderStatus()));

            //收件人信息
            json.put("receiverName", order.getReceiverName());
            json.put("receiverTel", order.getReceiverTel());
            StringBuilder receiverAddress = new StringBuilder();
            receiverAddress.append(order.getReceiverProvince());
            receiverAddress.append(order.getReceiverCity()==null?"":order.getReceiverCity());
            receiverAddress.append(order.getReceiverZone()==null?"":order.getReceiverZone());
            receiverAddress.append(order.getReceiverAddress()==null?"":order.getReceiverAddress());
            json.put("receiverAddress", receiverAddress.toString());

            data.add(json);
        }

        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }

    /**
     * @Description: 寄件订单详情(自行寄件，上门取件)
     * @Author: disvenk.dai
     * @Date: 2018/1/13
     */
    @RequestMapping(value = "/findSendOrder", method = {RequestMethod.POST})
    public ResponseEntity findSendOrder(@RequestBody IdForm form) throws Exception {
        ExpSendOrder order = saveSendExpressOrderService.findSendOrder(form.id);

        JSONObject json = new JSONObject();
        json.put("id", order.getId());//订单id
        json.put("orderNo", order.getOrderNo());//订单编号
        json.put("deliveryNo", order.getDeliveryNo());//快递单号
        json.put("deliveryCompanyId", order.getDeliveryId());//快递公司id
        json.put("deliveryCompanyName", order.getExpDeliveryCompany() == null ? "" : order.getExpDeliveryCompany().getName());//快递公司名称
        json.put("orderDate",order.getCreatedDate()==null?"": DateTimeUtils.parseStr(order.getCreatedDate()));//下单时间
        json.put("orderStatus", order.getOrderStatus());//订单状态id
        json.put("orderStatusName", Commo.parseOrderStatus(order.getOrderStatus()));//订单状态名称
        Payment payment = saveSendExpressOrderService.getPayment(form.id);
        if(payment!=null){
            json.put("payTime",payment.getFinishTime()==null?"": DateTimeUtils.parseStr(payment.getFinishTime().longValue()));
        }else {
            json.put("payTime","");
        }

        json.put("goosType",order.getGoodsTypeName());//物品类型
        json.put("deliverer",order.getExpUserDeliverer()==null?"":order.getExpUserDeliverer().getRelName());//上门人员名称
        json.put("delivererTel",order.getExpUserDeliverer()==null?"": order.getExpUserDeliverer().getUserMobile());//上门人员手机
        json.put("doorTime",order.getTodoorBeginEND()==null?"":order.getTodoorBeginEND());//上门时间
        json.put("takedTime",order.getExpressDate()==null?"": DateTimeUtils.parseStr(order.getExpressDate()));//取件时间
        json.put("toDoorFee",((order.getTodoorFee()==null) || (order.getTodoorFee()==0))?"": order.getTodoorFee());//上门费
        json.put("totalFee",((order.getOrderPrice()==null) || (order.getOrderPrice()==0))?"":order.getOrderPrice());//总价
        json.put("weight",order.getWeight());//重量
        json.put("remarks",order.getRemarks());

        //寄件人信息
        json.put("sendName", order.getSendName());
        json.put("sendTel", order.getSendTel());
        StringBuilder sendAddress = new StringBuilder();
        sendAddress.append(order.getSendProvince());
        sendAddress.append(order.getSendCity()==null?"":order.getSendCity());
        sendAddress.append(order.getSendZone()==null?"":order.getSendZone());
        sendAddress.append(order.getSendAddress()==null?"":order.getSendAddress());
        json.put("sendAddress",sendAddress.toString());

        //收件人信息
        json.put("receiverName", order.getReceiverName());
        json.put("receiverTel", order.getReceiverTel());
        StringBuilder receiverAddress = new StringBuilder();
        receiverAddress.append(order.getReceiverProvince());
        receiverAddress.append(order.getReceiverCity()==null?"":order.getReceiverCity());
        receiverAddress.append(order.getReceiverZone()==null?"":order.getReceiverZone());
        receiverAddress.append(order.getReceiverAddress()==null?"":order.getReceiverAddress());
        json.put("receiverAddress", receiverAddress.toString());
        ExpSchool expSchool= saveSendExpressOrderService.get2(ExpSchool.class,"id",CurrentUser.get().schoolId,"logicDeleted",false);
        json.put("ispaper",Commo.getIspaper(expSchool.getIspaper()));

        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }

    /**
     * @Description: 取消订单
     * @Author: disvenk.dai
     * @Date: 2018/1/19
     */
    @RequestMapping(value = "/cancelOrder",method = {RequestMethod.POST})
    public ResponseEntity cancelOrder(@RequestBody IdForm form) throws ResponseEntityException, UpsertException {
        saveSendExpressOrderService.cancelOrder(form.id);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 删除订单
     * @Author: disvenk.dai
     * @Date: 2018/1/19
     */
    @RequestMapping(value = "/deleteOrder",method = {RequestMethod.POST})
    public ResponseEntity deleteOrder(@RequestBody IdForm form) throws ResponseEntityException, UpsertException {
        saveSendExpressOrderService.deleteOrder(form.id);
        return new ResponseEntity(new RestResponseEntity(100, "成功",null), HttpStatus.OK);
    }



    /**
     * @Description:寄件订单保存快递单号
     * @Author: hanchao
     * @Date: 2018/1/31 0031
     */
    @RequestMapping(value = "/updateSendOrderDeliverNo", method = {RequestMethod.POST})
    public ResponseEntity updateSendOrderDeliverNo(@RequestBody IdForm form, HttpServletRequest request) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        if(form.deliveryName == null){
            return new ResponseEntity(new RestResponseEntity(130,"快递公司不能为空",null), HttpStatus.OK);
        }
        ExpDeliveryCompany expDeliveryCompany = saveSendExpressOrderService.get2(ExpDeliveryCompany.class,"name",form.deliveryName,"logicDeleted",false);
        ExpSchool exp = saveSendExpressOrderService.get2(ExpSchool.class,"id",CurrentUser.get().schoolId,"logicDeleted",false);
        if(exp == null){
            return new ResponseEntity(new RestResponseEntity(130,"网点信息不存在",null), HttpStatus.OK);
        }
//            0 电子， 1 纸质
        if(exp.getIspaper() == 1){
            saveSendExpressOrderService.updateSendOrderDeliverNo(form.id,form.deliveryNo,CurrentUser.get().schoolId,expDeliveryCompany.getId());
        }else{
            List<ExpDeliveryOrderNo> expOrderNoList = saveSendExpressOrderService.selectSendOrderDeliverNo(CurrentUser.get().schoolId,expDeliveryCompany.getId());
            if(expOrderNoList.size() == 0 || expOrderNoList == null){
                return new ResponseEntity(new RestResponseEntity(130,"系统暂未维护该网点或该快递公司快递单号已用完",null), HttpStatus.OK);
            }
            json.put("deliveryNo",expOrderNoList.get(0).getDeliveryNo());
            saveSendExpressOrderService.updateSendOrderDeliverNo(form.id,expOrderNoList.get(0).getDeliveryNo(),CurrentUser.get().schoolId,expDeliveryCompany.getId());
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }
}
