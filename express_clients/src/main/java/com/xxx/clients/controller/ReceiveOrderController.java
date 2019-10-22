package com.xxx.clients.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.clients.form.IdForm;
import com.xxx.clients.form.MyReceiveOrderForm;
import com.xxx.clients.service.ReceiveOrderService;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.ExpDeliveryCompany;
import com.xxx.model.business.ExpReceiveOrder;
import com.xxx.model.business.ExpSchoolDeliveryCompany;
import com.xxx.model.business.Payment;
import com.xxx.user.Commo;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.DateTimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/receiveOrder")
public class ReceiveOrderController {

    @Autowired
    private ReceiveOrderService receiveOrderService;

    /**
     * @Description: 学生端：保存至我的订单,保存收件信息(pass)
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @RequestMapping(value = "/saveToMyOrder", method = {RequestMethod.POST})
    public ResponseEntity saveToMyOrder(@RequestBody MyReceiveOrderForm form) throws Exception {
        if (form.deliverCompanyId == null)
            return new ResponseEntity(new RestResponseEntity(110, "快递公司必选", null), HttpStatus.OK);
        if (form.deliveryNo == null)
            return new ResponseEntity(new RestResponseEntity(120, "快递单号必输", null), HttpStatus.OK);

       Boolean flag = receiveOrderService.saveToMyOrder(form, CurrentUser.get().userId, CurrentUser.get().schoolId);
       if(flag){
           return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
       }
        return new ResponseEntity(new RestResponseEntity(110, "该快递单已存在系统", null), HttpStatus.OK);
    }

    /**
     * @Description: 收件订单列表(自行取件和派件上门)(pass)
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @RequestMapping(value = "/findReceiveOrderList", method = {RequestMethod.POST})
    public ResponseEntity findReceiveOrderList(@RequestBody IdForm form) throws Exception {
      /*  PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<ExpReceiveOrder> list = recieveOrderService.findRefuseList(pageQuery,form);*/
        List<ExpReceiveOrder> list = receiveOrderService.findReceiveOrderList(CurrentUser.get().userId, CurrentUser.get().schoolId, form.orderType);
        JSONArray data = new JSONArray();
        for (ExpReceiveOrder order : list) {
            JSONObject json = new JSONObject();
            json.put("id", order.getId());
            json.put("orderNo", order.getOrderNo());
            json.put("deliveryNo", order.getDeliveryNo());
            json.put("deliveryCompanyId", order.getDeliveryId());
            json.put("deliveryCompanyName", order.getExpDeliveryCompany() == null ? "" : order.getExpDeliveryCompany().getName());
            json.put("orderDate",  DateTimeUtils.parseStr(order.getCreatedDate()));
            json.put("orderStatus", order.getOrderStatus());
            json.put("orderStatusName", Commo.parseReceiveOrderStatus(order.getOrderStatus()));
            //收件人信息
            json.put("receiverName", order.getReceiverName());
            json.put("receiverTel", order.getReceiverTel());
            StringBuilder recieAddress = new StringBuilder();
            recieAddress.append( order.getProvince());
            recieAddress.append(order.getCity()==null?"":order.getCity());
            recieAddress.append(order.getZone()==null?"":order.getZone());
            recieAddress.append(order.getReceiverAddress()==null?"":order.getReceiverAddress());
            json.put("receiverAddress", recieAddress.toString());
            //物品说明
            json.put("goodsTypeName", order.getGoodsTypeName()==null?"":order.getGoodsTypeName());
            json.put("goodsDescription", order.getGoodsDescription()==null?"":order.getGoodsDescription());

            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }

    /**
     * @Description: 收件订单详情(自行取件和派件上门)(pass)
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @RequestMapping(value = "/getReceiveOrder", method = {RequestMethod.POST})
    public ResponseEntity getReceiveOrder(@RequestBody IdForm form) throws Exception {
        ExpReceiveOrder order = receiveOrderService.getReceiveOrder(form.id);
        JSONObject json = new JSONObject();
        json.put("id", order.getId());
        json.put("orderNo", order.getOrderNo());
        json.put("deliveryNo", order.getDeliveryNo());
        json.put("deliveryCompanyId", order.getDeliveryId());
        json.put("deliveryCompanyName", order.getExpDeliveryCompany() == null ? "" : order.getExpDeliveryCompany().getName());
        json.put("shortName",order.getExpDeliveryCompany().getCom());
        json.put("receiveName",order.getReceiverName());
        json.put("receiveTel",order.getReceiverTel());
        StringBuilder recieAddress = new StringBuilder();
        recieAddress.append( order.getProvince());
        recieAddress.append(order.getCity()==null?"":order.getCity());
        recieAddress.append(order.getZone()==null?"":order.getZone());
        recieAddress.append(order.getReceiverAddress()==null?"":order.getReceiverAddress());
        json.put("receiveAddress",recieAddress.toString());
        json.put("doorTime",DateTimeUtils.parseStr(order.getTodoorDate()));
        json.put("refuseTime",DateTimeUtils.parseStr(order.getRefuseDate()));
        json.put("doorTimeBeginEnd",order.getBeginEndTime());
            Payment payment = receiveOrderService.getPayment(form.id);
            if(payment!=null){
                json.put("payTime",DateTimeUtils.parseStr(payment.getFinishTime()));
            }else {
                json.put("payTime","");
            }
        if(order.getOrderStatus()==4){
            json.put("xiaoGe",order.getExpUserDeliverer().getRelName());
            json.put("xiaoGeTel",order.getExpUserDeliverer().getUserMobile());
        }
        json.put("signtTime",DateTimeUtils.parseStr(order.getExpressDate()));

        json.put("remarks",order.getRemarks());

        //下单时间
        json.put("orderDate", DateTimeUtils.parseStr(order.getCreatedDate()));
        //到达时间
        json.put("arrivedDate", DateTimeUtils.parseStr(order.getArrivedDate()));
        //签收时间
        json.put("expressDate", order.getExpressDate()==null?"":DateTimeUtils.parseStr(order.getExpressDate()));
        //拒收时间
        json.put("refuse",order.getOrderStatus()==null? "": DateTimeUtils.parseStr((order.getRefuseDate())));
        //验收码
        json.put("validateCode", order.getValidateCode());
        json.put("orderStatus", order.getOrderStatus());
        json.put("orderStatusName", Commo.parseReceiveOrderStatus(order.getOrderStatus()));
        //备注
        json.put("remarks", order.getRemarks());
        //支付相关

        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }

    /**
     * @Description: 取消收件订单(pass)
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @RequestMapping(value = "/deleteReceiveOrder", method = {RequestMethod.POST})
    public ResponseEntity deleteReceiveOrder(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        receiveOrderService.deleteReceiveOrder(CurrentUser.get().userId, form.id);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 拒收订单(pass)
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @RequestMapping(value = "/rejectOrder", method = {RequestMethod.POST})
    public ResponseEntity rejectOrder(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        receiveOrderService.rejectOrder(CurrentUser.get().userId, form.id);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 确认送达
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @RequestMapping(value = "/receivedOrder", method = {RequestMethod.POST})
    public ResponseEntity receivedOrder(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        receiveOrderService.receivedOrder(CurrentUser.get().userId, form.id);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }


}
