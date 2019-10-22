package com.xxx.merchant.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.merchant.form.IdForm;
import com.xxx.merchant.form.SendOrderForm;
import com.xxx.merchant.form.SendOrderGoDoorForm;
import com.xxx.merchant.form.SendOrderPriceForm;
import com.xxx.merchant.service.SendExpressOrderService;
import com.xxx.model.business.*;
import com.xxx.user.Commo;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.utils.SessionUtils;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Controller("sendOrder2")
@RequestMapping("/sendOrder")
public class SendExpressOrderController {

    @Autowired
    private SendExpressOrderService sendExpressOrderService;

   /**
    * @Description:寄件订单的自行寄件
    * @Author: hanchao
    * @Date: 2018/1/17 0017
    */
    @RequestMapping(value = "/findSendOrderByMySelfList", method = {RequestMethod.POST})
    public ResponseEntity findSendOrderByMySelfList(@RequestBody SendOrderForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        if (form.type == null)
            return new ResponseEntity(new RestResponseEntity(120, "订单类型不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<ExpSendOrder> list = sendExpressOrderService.findSendOrderByMySelfList(pageQuery,CurrentUser.get().userId,CurrentUser.get().schoolId,form.type, form.receiverName);
        JSONArray data = new JSONArray();
        for (ExpSendOrder exp : list) {
            JSONObject json = new JSONObject();
            json.put("id", exp.getId());
            json.put("orderNo", exp.getOrderNo());
            json.put("orderDate", DateTimeUtils.parseStr(exp.getCreatedDate()));
            json.put("orderStatus", exp.getOrderStatus());
            json.put("orderStatusName", Commo.parseOrderStatus(exp.getOrderStatus()));
            json.put("delivererId",exp.getDelivererId());//上门人员id
            if(exp.getDelivererId() != null){
                ExpUser us = sendExpressOrderService.get2(ExpUser.class,"id",exp.getDelivererId(),"logicDeleted",false);
                if(us != null){
                    json.put("delivererName",us.getUserCode());
                }
            }
            //收件人信息
            json.put("receiverName", exp.getReceiverName());
            json.put("receiverTel", exp.getReceiverTel());
            StringBuilder sb = new StringBuilder();
            sb.append(exp.getReceiverProvince() == null ? "" : exp.getReceiverProvince());
            sb.append(exp.getReceiverCity() == null ? "" : exp.getReceiverCity());
            sb.append(exp.getReceiverZone() == null ? "" : exp.getReceiverZone());
            sb.append(exp.getReceiverAddress() == null ? "" : exp.getReceiverAddress());
            json.put("receiverAddress",sb.toString());
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功",data, pageQuery.page, pageQuery.limit, list.total), HttpStatus.OK);
    }

    /**
     * @Description:寄件订单的上门取件
     * @Author: hanchao
     * @Date: 2018/1/27 0027
     */
    @RequestMapping(value = "/findSendOrderByToDoorMySelfList", method = {RequestMethod.POST})
    public ResponseEntity findSendOrderByToDoorMySelfList(@RequestBody SendOrderForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        if (form.type == null)
            return new ResponseEntity(new RestResponseEntity(120, "订单类型不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<ExpSendOrder> list = sendExpressOrderService.findSendOrderByToDoorMySelfList(pageQuery,CurrentUser.get().userId, CurrentUser.get().schoolId,form.type, form.receiverName,form.status);
        JSONArray data = new JSONArray();
        for (ExpSendOrder exp : list) {
            JSONObject json = new JSONObject();
            json.put("id", exp.getId());
            json.put("orderNo", exp.getOrderNo());
            json.put("orderDate", DateTimeUtils.parseStr(exp.getCreatedDate()));
            json.put("orderStatus", exp.getOrderStatus());
            json.put("orderStatusName", Commo.parseOrderStatus(exp.getOrderStatus()));
            json.put("delivererId",exp.getDelivererId());//上门人员id
            if(exp.getDelivererId() != null){
                ExpUser us = sendExpressOrderService.get2(ExpUser.class,"id",exp.getDelivererId(),"logicDeleted",false);
                if(us != null){
                    json.put("delivererName",us.getUserCode());
                }
            }
            //收件人信息
            json.put("receiverName", exp.getReceiverName());
            json.put("receiverTel", exp.getReceiverTel());
            StringBuilder sb = new StringBuilder();
            sb.append(exp.getReceiverProvince() == null ? "" : exp.getReceiverProvince());
            sb.append(exp.getReceiverCity() == null ? "" : exp.getReceiverCity());
            sb.append(exp.getReceiverZone() == null ? "" : exp.getReceiverZone());
            sb.append(exp.getReceiverAddress() == null ? "" : exp.getReceiverAddress());
            json.put("receiverAddress",sb);
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功",data, pageQuery.page, pageQuery.limit, list.total), HttpStatus.OK);
    }

    /**
     * @Description:根据物品重量和快递公司计算运费(自行寄件)
     * @Author: hanchao
     * @Date: 2018/1/18 0018
     */
    @RequestMapping(value = "/sendOrderPriceByMySelfWeightAndDelivery", method = {RequestMethod.POST})
    public ResponseEntity sendOrderPriceByMySelfWeightAndDelivery(@RequestBody SendOrderPriceForm form) throws Exception {
        ExpDeliveryCompany expDeliveryCompany = sendExpressOrderService.get2(ExpDeliveryCompany.class,"name",form.deliveryName,"logicDeleted",false);
        if(expDeliveryCompany == null){
            return new ResponseEntity(new RestResponseEntity(120, "快递公司不存在", null), HttpStatus.OK);
        }
        ExpSchoolDeliveryCompany expSchoolDeliveryCompany = sendExpressOrderService.get2(ExpSchoolDeliveryCompany.class,"schoolId",CurrentUser.get().schoolId,"deliveryId",expDeliveryCompany.getId(),"logicDeleted",false);
        if(expSchoolDeliveryCompany == null){
            return new ResponseEntity(new RestResponseEntity(110, "系统暂未维护该网点和当前快递公司", null), HttpStatus.OK);
        }
        ExpProvinceServiceFee exp = sendExpressOrderService.sendOrderPriceByWeightAndDelivery(expSchoolDeliveryCompany.getId(),form.receiverProvince);
        if(exp == null){
            return new ResponseEntity(new RestResponseEntity(140, "系统暂未维护该省和该快递公司的运费价格", null), HttpStatus.OK);
        }
        JSONObject json = new JSONObject();
        if(form.weight == null){
            return new ResponseEntity(new RestResponseEntity(130, "货品重量不能为空", null), HttpStatus.OK);
        }
        Double weight = Double.parseDouble(String.valueOf(form.weight).substring(0,String.valueOf(form.weight).lastIndexOf(".")));
        if(form.weight<=1){
            json.put("price",MathUtils.formatObjectReturnDouble(exp.getFirstWeightFee() == null ? 0 : exp.getFirstWeightFee()));
            json.put("costPrice",MathUtils.formatObjectReturnDouble(exp.getFirstCostFee() == null ? 0 : exp.getFirstCostFee()));
        }else if(form.weight > weight){
            json.put("price",MathUtils.formatObjectReturnDouble(exp.getFirstWeightFee() == null ? 0 : exp.getFirstWeightFee() + (exp.getOtherWeightFee() == null ? 0 : exp.getOtherWeightFee()) * weight));
            json.put("costPrice",MathUtils.formatObjectReturnDouble((exp.getFirstCostFee() == null ? 0 : exp.getFirstCostFee() ) + (exp.getOtherCostFee() == null ? 0 : exp.getOtherCostFee() ) * weight));
        }else{
            json.put("price",MathUtils.formatObjectReturnDouble(exp.getFirstWeightFee() == null ? 0 : exp.getFirstWeightFee() + (exp.getOtherWeightFee() == null ? 0 : exp.getOtherWeightFee()) * (weight-1)));
            json.put("costPrice",MathUtils.formatObjectReturnDouble((exp.getFirstCostFee() == null ? 0 : exp.getFirstCostFee() ) + (exp.getOtherCostFee() == null ? 0 : exp.getOtherCostFee() ) * (weight-1)));
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

    /**
    * @Description:寄件订单的自行寄件(待取件,已支付)详情
    * @Author: hanchao
    * @Date: 2018/1/17 0017
    */
    @RequestMapping(value = "/findSendOrderByMySelfDetail", method = {RequestMethod.POST})
    public ResponseEntity findSendOrderByMySelfDetail(@RequestBody IdForm form) throws Exception {
        if(form.id == null){
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        }
        ExpSendOrder exp = sendExpressOrderService.selectSendOrderByMySelfDetail(CurrentUser.get().schoolId,form.id,form.orderStatus);
        if(exp == null){
            return new ResponseEntity(new RestResponseEntity(120, "订单信息不存在", null), HttpStatus.OK);
        }
            JSONObject json = new JSONObject();
            json.put("id", exp.getId());
            json.put("orderNo", exp.getOrderNo());//订单编号
            json.put("orderDate", DateTimeUtils.parseStr(exp.getCreatedDate()));//下单时间
            json.put("expressDate", DateTimeUtils.parseStr(exp.getExpressDate()));//取件时间
            json.put("payTime","");//支付时间
            Payment pay = sendExpressOrderService.get2(Payment.class,"orderId",exp.getId(),"status",20,"orderType",2);
            if(pay != null){
                json.put("payTime",DateTimeUtils.parseStr(pay.getFinishTime()));
            }
            json.put("deliveryNo",exp.getDeliveryNo());//快递单号
            json.put("orderStatus", exp.getOrderStatus());
            json.put("orderStatusName", Commo.parseOrderStatus(exp.getOrderStatus()));
            //收件人信息
            json.put("receiverName", exp.getReceiverName());
            json.put("receiverTel", exp.getReceiverTel());
            json.put("receiverProvince", exp.getReceiverProvince());
            StringBuilder sb = new StringBuilder();
            sb.append(exp.getReceiverProvince() == null ? "" : exp.getReceiverProvince());
            sb.append(exp.getReceiverCity() == null ? "" : exp.getReceiverCity());
            sb.append(exp.getReceiverZone() == null ? "" : exp.getReceiverZone());
            sb.append(exp.getReceiverAddress() == null ? "" : exp.getReceiverAddress());
            json.put("receiverAddress",sb.toString());
            //寄件人信息
            json.put("sendName",exp.getSendName());/*寄件人名称*/
            json.put("sendTel",exp.getSendTel());/*寄件人电话*/
            StringBuilder sb1 = new StringBuilder();
           sb1.append(exp.getSendProvince() == null ? "" : exp.getSendProvince());
            sb1.append(exp.getSendCity() == null ? "" : exp.getSendCity());
            sb1.append(exp.getSendZone() == null ? "" : exp.getSendZone());
            sb1.append(exp.getSendAddress() == null ? "" : exp.getSendAddress());
            json.put("sendAddress",sb1.toString());/*寄件人地址*/
            //json.put("sendAddress",exp.getSendAddress());/*寄件人地址*/
            json.put("sendProvince",exp.getSendProvince());
            json.put("goodsTypeId",exp.getGoodsTypeId());/*物品类型id*/
            json.put("weight",exp.getWeight());/*物品重量*/
            json.put("goodsTypeName",exp.getGoodsTypeName());/*物品类型*/
            json.put("deliveryName",exp.getExpDeliveryCompany() == null ? "" : exp.getExpDeliveryCompany().getName());
            json.put("price",MathUtils.formatObjectReturnDouble(exp.getPrice()));//价格
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }

    /**
     * @Description:寄件订单的自行寄件详情页(待取件)确认收件
     * @Author: hanchao
     * @Date: 2018/1/18 0018
     */
    @RequestMapping(value = "/updateSendOrderByMySelf", method = {RequestMethod.POST})
    public ResponseEntity updateExpSendOrder(@RequestBody SendOrderForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        sendExpressOrderService.updateSendOrderByMySelf(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:寄件订单的上门取件(待取件,已支付)详情
     * @Author: hanchao
     * @Date: 2018/1/17 0017
     */
    @RequestMapping(value = "/findSendOrderByGoDoorDetail", method = {RequestMethod.POST})
    public ResponseEntity findSendOrderByGoDoorDetail(@RequestBody IdForm form) throws Exception {
        if(form.id == null){
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        }
        ExpSendOrder exp = sendExpressOrderService.selectSendOrderByMySelfDetail(CurrentUser.get().schoolId,form.id,form.orderStatus);
        if(exp == null){
            return new ResponseEntity(new RestResponseEntity(120, "订单信息不存在", null), HttpStatus.OK);
        }
        JSONObject json = new JSONObject();
        json.put("id", exp.getId());
        json.put("orderNo", exp.getOrderNo());//订单编号
        json.put("orderDate", DateTimeUtils.parseStr(exp.getCreatedDate()));//下单时间
        json.put("expressDate", DateTimeUtils.parseStr(exp.getExpressDate()));//取件时间
        json.put("todoorBeginEND", exp.getTodoorBeginEND());//上门时间
        json.put("payTime","");//支付时间
        Payment pay = sendExpressOrderService.get2(Payment.class,"orderId",exp.getId(),"status",20,"orderType",2);
        if(pay != null){
            json.put("payTime",DateTimeUtils.parseStr(pay.getFinishTime()));
        }
        json.put("deliveryNo",exp.getDeliveryNo());//快递单号
        json.put("orderStatus", exp.getOrderStatus());
        json.put("orderStatusName", Commo.parseOrderStatus(exp.getOrderStatus()));//订单状态
        json.put("delivererId",exp.getDelivererId());//上门人员id
        if(exp.getDelivererId() != null){
            ExpUser us = sendExpressOrderService.get2(ExpUser.class,"id",exp.getDelivererId(),"logicDeleted",false);
            if(us != null){
                json.put("delivererName",us.getUserCode());
            }
        }
        json.put("goodsTypeId",exp.getGoodsTypeId());/*物品类型id*/
        json.put("weight",exp.getWeight());/*物品重量*/
        json.put("goodsTypeName",exp.getGoodsTypeName());/*物品类型*/
        json.put("deliveryName",exp.getExpDeliveryCompany() == null ? "" : exp.getExpDeliveryCompany().getName());
        json.put("price",exp.getPrice());//快递价格
        json.put("todoorFee",exp.getTodoorFee());//上门费
        //json.put("todoorTipFee",exp.getTodoorTipFee());//上门小费
        ExpServiceFee fee = sendExpressOrderService.get2(ExpServiceFee.class,"schoolId",CurrentUser.get().schoolId,"type",2,"logicDeleted",false);
        if(fee != null){
            json.put("todoorFee",fee.getFee());//上门费
        }
        json.put("orderPrice",MathUtils.formatObjectReturnDouble(exp.getOrderPrice()));//价格(快递价格 + 上门小费)
        json.put("remark",exp.getRemarks());
        //收件人信息
        json.put("receiverName", exp.getReceiverName());
        json.put("receiverTel", exp.getReceiverTel());
        json.put("receiveProvince",exp.getReceiverProvince());
        json.put("receiveCity",exp.getReceiverCity());
        json.put("receiveZone",exp.getReceiverZone());
        StringBuilder sb = new StringBuilder();
        sb.append(exp.getReceiverProvince() == null ? "" : exp.getReceiverProvince());
        sb.append(exp.getReceiverCity() == null ? "" : exp.getReceiverCity());
        sb.append(exp.getReceiverZone() == null ? "" : exp.getReceiverZone());
        sb.append(exp.getReceiverAddress() == null ? "" : exp.getReceiverAddress());
        json.put("receiverAddress",sb.toString());
        //寄件人信息
        json.put("sendName",exp.getSendName());/*寄件人名称*/
        json.put("sendTel",exp.getSendTel());/*寄件人电话*/
        json.put("sendProvince",exp.getSendProvince());
        json.put("sendCity",exp.getSendCity());
        json.put("sendZone",exp.getSendZone());
        StringBuilder sb1 = new StringBuilder();
        sb1.append(exp.getSendProvince() == null ? "" : exp.getSendProvince());
        sb1.append(exp.getSendCity() == null ? "" : exp.getSendCity());
        sb1.append(exp.getSendZone() == null ? "" : exp.getSendZone());
        sb1.append(exp.getSendAddress() == null ? "" : exp.getSendAddress());
        json.put("sendAddress",sb1.toString());/*寄件人地址*/
        ExpSchool expSchool= sendExpressOrderService.get2(ExpSchool.class,"id",CurrentUser.get().schoolId,"logicDeleted",false);
        json.put("ispaper",Commo.getIspaper(expSchool.getIspaper()));
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }
    
    /**
     * @Description:根据物品重量和快递公司计算运费(上门取件)
     * @Author: hanchao
     * @Date: 2018/1/18 0018
     */
    @RequestMapping(value = "/sendOrderPriceByWeightAndDelivery", method = {RequestMethod.POST})
    public ResponseEntity sendOrderPriceByWeightAndDelivery(@RequestBody SendOrderPriceForm form) throws Exception {
        ExpDeliveryCompany expDeliveryCompany = sendExpressOrderService.get2(ExpDeliveryCompany.class,"name",form.deliveryName,"logicDeleted",false);
        if(expDeliveryCompany == null){
            return new ResponseEntity(new RestResponseEntity(120, "快递公司不存在", null), HttpStatus.OK);
        }
        ExpSchoolDeliveryCompany expSchoolDeliveryCompany = sendExpressOrderService.get2(ExpSchoolDeliveryCompany.class,"schoolId",CurrentUser.get().schoolId,"deliveryId",expDeliveryCompany.getId(),"logicDeleted",false);
        if(expSchoolDeliveryCompany == null){
            return new ResponseEntity(new RestResponseEntity(110, "系统暂未维护该网点和当前快递公司", null), HttpStatus.OK);
        }
        ExpServiceFee fee = sendExpressOrderService.get2(ExpServiceFee.class,"schoolId",CurrentUser.get().schoolId,"type",2,"logicDeleted",false);
        if(fee == null){
            return new ResponseEntity(new RestResponseEntity(120, "系统暂未维护该网点的上门费", null), HttpStatus.OK);
        }
        ExpProvinceServiceFee exp = sendExpressOrderService.sendOrderPriceByWeightAndDelivery(expSchoolDeliveryCompany.getId(),form.receiverProvince);
        if(exp == null){
            return new ResponseEntity(new RestResponseEntity(140, "系统暂未维护该省和该快递公司的运费价格", null), HttpStatus.OK);
        }
        JSONObject json = new JSONObject();
        Double todoorTipFee = 0.0;
        if(form.id != null){
            ExpSendOrder sendOrder = sendExpressOrderService.get2(ExpSendOrder.class,"id",form.id,"logicDeleted",false);
            todoorTipFee = sendOrder.getTodoorTipFee() == null ? 0 : sendOrder.getTodoorTipFee();
        }
        if(form.weight == null){
            return new ResponseEntity(new RestResponseEntity(130, "货品重量不能为空", null), HttpStatus.OK);
        }
        Double weight = Double.parseDouble(String.valueOf(form.weight).substring(0,String.valueOf(form.weight).lastIndexOf(".")));
        if(form.weight<=1){
            json.put("price",MathUtils.formatObjectReturnDouble(exp.getFirstWeightFee() == null ? 0 : exp.getFirstWeightFee() + fee.getFee() + todoorTipFee));//快递费+上门费(不包含上门小费)
            json.put("costPrice",MathUtils.formatObjectReturnDouble(exp.getFirstCostFee() == null ? 0 : exp.getFirstCostFee()));//成本
        }else if(form.weight > weight){
            json.put("price",MathUtils.formatObjectReturnDouble(exp.getFirstWeightFee() == null ? 0 : exp.getFirstWeightFee() + (exp.getOtherWeightFee() == null ? 0 : exp.getOtherWeightFee()) * weight + fee.getFee() + todoorTipFee));
            json.put("costPrice",MathUtils.formatObjectReturnDouble((exp.getFirstCostFee() == null ? 0 : exp.getFirstCostFee() ) + (exp.getOtherCostFee() == null ? 0 : exp.getOtherCostFee() ) * weight));
        }else{
            json.put("price",MathUtils.formatObjectReturnDouble(exp.getFirstWeightFee() == null ? 0 : exp.getFirstWeightFee() + (exp.getOtherWeightFee() == null ? 0 : exp.getOtherWeightFee()) * (weight-1) + fee.getFee() + todoorTipFee));
            json.put("costPrice", MathUtils.formatObjectReturnDouble((exp.getFirstCostFee() == null ? 0 : exp.getFirstCostFee() ) + (exp.getOtherCostFee() == null ? 0 : exp.getOtherCostFee() ) * (weight-1)));
        }
        json.put("fee",fee.getFee());//上门费
        json.put("todoorTipFee",todoorTipFee);//上门小费
        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

    /**
     * @Description:寄件订单的上门取件我要上门
     * @Author: hanchao
     * @Date: 2018/1/18 0018
     */
    @RequestMapping(value = "/updateSendOrderByGoDoor", method = {RequestMethod.POST})
    public ResponseEntity updateSendOrderByGoDoor(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        sendExpressOrderService.updateSendOrderByGoDoor(form.id,CurrentUser.get().userId);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:寄件订单的上门取件确认取件
     * @Author: hanchao
     * @Date: 2018/1/18 0018
     */
    @RequestMapping(value = "/updateSendOrderByReceive", method = {RequestMethod.POST})
    public ResponseEntity updateSendOrderByReceive(@RequestBody SendOrderGoDoorForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        sendExpressOrderService.updateSendOrderByReceive(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:寄件订单的上门取件详情的确认取件
     * @Author: hanchao
     * @Date: 2018/1/29 0029
     */
    @RequestMapping(value = "/updateSendOrderDetailByReceive", method = {RequestMethod.POST})
    public ResponseEntity updateSendOrderDetailByReceive(@RequestBody SendOrderGoDoorForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        sendExpressOrderService.updateSendOrderDetailByReceive(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
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
        ExpDeliveryCompany expDeliveryCompany = sendExpressOrderService.get2(ExpDeliveryCompany.class,"name",form.deliveryName,"logicDeleted",false);
        ExpSchool exp = sendExpressOrderService.get2(ExpSchool.class,"id",CurrentUser.get().schoolId,"logicDeleted",false);
        if(exp == null){
            return new ResponseEntity(new RestResponseEntity(130,"网点信息不存在",null), HttpStatus.OK);
        }
//            0 电子， 1 纸质
        if(exp.getIspaper() == 1){
            sendExpressOrderService.updateSendOrderDeliverNoIspaper(form.id,form.deliveryNo);
        }else{
//            List<ExpDeliveryOrderNo> expOrderNoList = sendExpressOrderService.selectSendOrderDeliverNo(CurrentUser.get().schoolId,expDeliveryCompany.getId());
//            if(expOrderNoList.size() == 0 || expOrderNoList == null){
//                return new ResponseEntity(new RestResponseEntity(130,"系统暂未维护该网点或该快递公司快递单号已用完",null), HttpStatus.OK);
//            }
//            json.put("deliveryNo",expOrderNoList.get(0).getDeliveryNo());
//            sendExpressOrderService.updateSendOrderDeliverNo(form.id,expOrderNoList.get(0).getDeliveryNo(),CurrentUser.get().schoolId,expDeliveryCompany.getId());
            sendExpressOrderService.updateSendOrderDeliverNo(form.id,form.deliveryNo,CurrentUser.get().schoolId,expDeliveryCompany.getId());
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }
}
