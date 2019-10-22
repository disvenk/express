package com.xxx.merchant.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.merchant.form.ExpressQueryForm;
import com.xxx.merchant.form.IdForm;
import com.xxx.merchant.form.MyReceiveOrderForm;
import com.xxx.merchant.service.RecieveOrderService;
import com.xxx.model.business.*;
import com.xxx.user.Commo;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.criteria.From;
import java.util.List;

@Controller("recieveOrder2")
@RequestMapping("recieveOrder")
public class RecieveOrderController {

    @Autowired
    private RecieveOrderService recieveOrderService;

    /**
     * @Description: 录入查询
     * @Author: disvenk.dai
     * @Date: 2018/1/18
     */
    @RequestMapping(value = "/enterCheck",method = RequestMethod.POST)
    public ResponseEntity enterCheck(@RequestBody IdForm form){
        ExpReceiveOrder order = recieveOrderService.enterCheck(form.deliveryNo, CurrentUser.get().schoolId);
        JSONObject json = null;
        if(order!=null){
             json = new JSONObject();
            json.put("id", order.getId());
            json.put("deliveryNo", order.getDeliveryNo()==null?"":order.getDeliveryNo());
            json.put("deliveryCompanyName", order.getExpDeliveryCompany() == null ? "" : order.getExpDeliveryCompany().getName());
            ExpUser expUser = recieveOrderService.get2(ExpUser.class,"id",order.getUserId());
            json.put("createByTel",expUser == null ? "" : expUser.getUserMobile());
            if(order.getOrderStatus()==3){
                json.put("xiaoGe",order.getExpUserDeliverer() == null ? "" : order.getExpUserDeliverer().getRelName());
                json.put("xiaoGeTel",order.getExpUserDeliverer() == null ? "" : order.getExpUserDeliverer().getUserMobile());
            }
            json.put("remarks",order.getRemarks()==null?"":order.getRemarks());
            json.put("goodType",order.getGoodsTypeName()==null?"":order.getGoodsTypeName());
        }

        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);

    }

    /**
     * @Description: 录入后更新状态或者下单
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @RequestMapping(value = "/enterUpdate", method = {RequestMethod.POST})
    public ResponseEntity enterUpdate(@RequestBody MyReceiveOrderForm form) throws Exception {
        if (form.deliveryCompanyName == null)
            return new ResponseEntity(new RestResponseEntity(110, "快递公司必选", null), HttpStatus.OK);
        if (form.deliveryNo == null)
            return new ResponseEntity(new RestResponseEntity(120, "快递单号必输", null), HttpStatus.OK);
        if (form.tel == null || StringUtils.isBlank(form.tel))
            return new ResponseEntity(new RestResponseEntity(120, "手机号必输", null), HttpStatus.OK);

             recieveOrderService.enterUpdate(form,CurrentUser.get().schoolId);
            return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 自行取件列表
     * @Author: disvenk.dai
     * @Date: 2018/1/18
     */
    @RequestMapping(value = "/findselfTake",method = RequestMethod.POST)
    public ResponseEntity findselfTake(@RequestBody ExpressQueryForm form){
        if(form.pageNum==null)
            return  new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);

        if(form.orderType==null){
            return  new ResponseEntity(new RestResponseEntity(110, "订单类型不能为空", null), HttpStatus.OK);
        }
        PageQuery pageQuery = new PageQuery(form.pageNum);
       PageList<ExpReceiveOrder> list = recieveOrderService.findSelfTake(pageQuery,form);
       JSONArray data = new JSONArray();
       if(list!=null && !list.isEmpty()){
        for(ExpReceiveOrder expReceiveOrder : list){
            JSONObject json = new JSONObject();
            json.put("id",expReceiveOrder.getId());
            json.put("deliveryNo",expReceiveOrder.getDeliveryNo());
            json.put("orderNo",expReceiveOrder.getOrderNo());
            json.put("orderStatus",Commo.parseReceiveOrderStatus(expReceiveOrder.getOrderStatus()));

            json.put("tel",expReceiveOrder.getReceiverTel());
            json.put("deliveryName",expReceiveOrder.getExpDeliveryCompany()==null?"":expReceiveOrder.getExpDeliveryCompany().getName());
            json.put("createdDate", DateTimeUtils.parseStr(expReceiveOrder.getCreatedDate()));
            json.put("arrivedDate",DateTimeUtils.parseStr(expReceiveOrder.getArrivedDate()));
            //json.put("arrivedDate",DateTimeUtils.parseStr(expReceiveOrder.get));
            json.put("validateCode",expReceiveOrder.getValidateCode());
            data.add(json);
        }
       }

        return new ResponseEntity(new PageResponseEntity(100, "成功",data, pageQuery.page, pageQuery.limit, list.total), HttpStatus.OK);
    }

    /**
     * @Description: 自行取件详情
     * @Author: disvenk.dai
     * @Date: 2018/1/18
     */
    @RequestMapping(value = "/selfTakeDetail",method = RequestMethod.POST)
    public ResponseEntity selfTakeDetail(@RequestBody IdForm form) throws ResponseEntityException {
        if(form.id==null){
            return  new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        }
        ExpReceiveOrder order = recieveOrderService.selfTakeDetail(form.id);
      ExpUser expUser = recieveOrderService.get2(ExpUser.class,"id",order.getUserId());
        JSONObject json = new JSONObject();
        json.put("id", order.getId());
        json.put("orderNo", order.getOrderNo());
        json.put("deliveryNo", order.getDeliveryNo());
        json.put("deliveryCompanyId", order.getDeliveryId());
        json.put("deliveryCompanyName", order.getExpDeliveryCompany() == null ? "" : order.getExpDeliveryCompany().getName());
        json.put("shortName",order.getExpDeliveryCompany().getCom());
        json.put("receiveName",order.getReceiverName()==null?"":order.getReceiverName());
        json.put("receiveTel",order.getReceiverTel()==null?"":order.getReceiverTel());//收件人
        json.put("userMobile",expUser.getUserMobile());//用户手机号
        if(order.getCapture()!=null){
            json.put("img", OSSClientUtil.getObjectUrl(order.getCapture()));
        }

        //到达时间
        json.put("arrivedDate",order.getArrivedDate()==null?"":DateTimeUtils.parseStr(order.getArrivedDate()));
        json.put("doorTimeBeginEnd",order.getBeginEndTime());

        Payment payment = recieveOrderService.getPayment(form.id);
        if(payment!=null){
            json.put("payTime",DateTimeUtils.parseStr(payment.getFinishTime()));
        }else {
            json.put("payTime","");
        }
        if(order.getOrderStatus()==3){
            json.put("xiaoGe",order.getExpUserDeliverer()==null?"":order.getExpUserDeliverer().getRelName());
            json.put("xiaoGeTel",order.getExpUserDeliverer()==null?"":order.getExpUserDeliverer().getUserMobile());
        }
        json.put("signtTime",DateTimeUtils.parseStr(order.getExpressDate()));

        json.put("remarks",order.getRemarks());

        //下单时间
        json.put("orderDate", DateTimeUtils.parseStr(order.getCreatedDate()));
        //送达时间
        json.put("reachDate", DateTimeUtils.parseStr(order.getReachDate()));
        //签收时间
        json.put("expressDate", DateTimeUtils.parseStr(order.getExpressDate()));
        //拒收时间
        json.put("refuseTime",order.getRefuseDate()==null? "": DateTimeUtils.parseStr((order.getRefuseDate())));
        //验收码
        json.put("validateCode", order.getValidateCode()==null?"":order.getValidateCode());
        json.put("orderStatus", order.getOrderStatus());
        json.put("orderStatusName", Commo.parseReceiveOrderStatus(order.getOrderStatus()));

        ExpSchool exp = recieveOrderService.get2(ExpSchool.class,"id",CurrentUser.get().schoolId,"logicDeleted",false);
        json.put("capture",exp.getCapture());//0否


        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);

    }

    /**
     * @Description: 自行取件部分确认签收
     * @Author: disvenk.dai
     * @Date: 2018/1/13
     */
    @RequestMapping(value = "/sureSign",method = {RequestMethod.POST})
    public ResponseEntity sureSign(@RequestBody IdForm form) throws ResponseEntityException, UpsertException {
    recieveOrderService.sureSign(form.id,form.base64);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 拒收订单列表
     * @Author: disvenk.dai
     * @Date: 2018/1/13
     */
    @RequestMapping(value = "/findRefuseList",method = {RequestMethod.POST})
    public ResponseEntity findRefuseList(@RequestBody IdForm form){
        if(form.pageNum==null){
            return  new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        }
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<ExpReceiveOrder> list = recieveOrderService.findRefuseList(pageQuery,form);
        JSONArray data = new JSONArray();
        for (ExpReceiveOrder expReceiveOrder:list ) {
            JSONObject json = new JSONObject();
            json.put("id",expReceiveOrder.getId());
            json.put("orderNo",expReceiveOrder.getOrderNo());
            json.put("orderStatus",Commo.parseReceiveOrderStatus(expReceiveOrder.getOrderStatus()));
            json.put("orderStatus",Commo.parseReceiveOrderStatus(expReceiveOrder.getOrderStatus()));
            json.put("deliveryNo",expReceiveOrder.getDeliveryNo());
            json.put("createDate",DateTimeUtils.parseStr(expReceiveOrder.getCreatedDate()));
            json.put("expressCompany",expReceiveOrder.getExpDeliveryCompany().getName());
            json.put("refuseDate",expReceiveOrder.getRefuseDate()==null?"":DateTimeUtils.parseStr(expReceiveOrder.getRefuseDate()));
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功",data, pageQuery.page, pageQuery.limit, list.total), HttpStatus.OK);
    }

    /**
     * @Description: 拒收订单详情
     * @Author: disvenk.dai
     * @Date: 2018/1/13
     */
    @RequestMapping(value = "/findRefuse",method = {RequestMethod.POST})
    public ResponseEntity findRefuse(@RequestBody IdForm form){
        if(form.id==null){
            return  new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        }

        ExpReceiveOrder expReceiveOrder = recieveOrderService.findRefuse(form.id);
            JSONObject json = new JSONObject();
            json.put("orderNo",expReceiveOrder.getOrderNo());
            json.put("orderStatus",expReceiveOrder.getOrderStatus());
            json.put("deliveryNo",expReceiveOrder.getDeliveryNo());
            json.put("arrivedDate",DateTimeUtils.parseStr(expReceiveOrder.getArrivedDate()));
            json.put("createDate",DateTimeUtils.parseStr(expReceiveOrder.getCreatedDate()));
            json.put("validateCode",expReceiveOrder.getValidateCode()==null?"":expReceiveOrder.getValidateCode());
            json.put("expressCompany",expReceiveOrder.getExpDeliveryCompany().getName());
            json.put("refuseDate",expReceiveOrder.getRefuseDate()==null?"":DateTimeUtils.parseStr(expReceiveOrder.getRefuseDate()));
        return new ResponseEntity(new RestResponseEntity(100, "成功",json), HttpStatus.OK);
    }

    /**
     * @Description: 退回订单列表
     * @Author: disvenk.dai
     * @Date: 2018/1/13
     */
    @RequestMapping(value = "/findReturnList",method = {RequestMethod.POST})
    public ResponseEntity findReturnList(@RequestBody IdForm form){
        if(form.pageNum==null){
            return  new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        }
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<ExpReceiveOrder> list = recieveOrderService.findReturnList(pageQuery,form);
        JSONArray data = new JSONArray();
        for (ExpReceiveOrder expReceiveOrder:list ) {
            JSONObject json = new JSONObject();
            json.put("id",expReceiveOrder.getId());
            json.put("orderNo",expReceiveOrder.getOrderNo());
            json.put("orderStatus",Commo.parseReceiveOrderStatus(expReceiveOrder.getOrderStatus()));
            json.put("deliveryNo",expReceiveOrder.getDeliveryNo());
            json.put("arrivedDate",DateTimeUtils.parseStr(expReceiveOrder.getArrivedDate()));
            json.put("createDate",DateTimeUtils.parseStr(expReceiveOrder.getCreatedDate()));
            json.put("expressCompany",expReceiveOrder.getExpDeliveryCompany().getName());
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功",data, pageQuery.page, pageQuery.limit, list.total), HttpStatus.OK);
    }

    /**
     * @Description: 退回订单详情
     * @Author: disvenk.dai
     * @Date: 2018/1/13
     */
    @RequestMapping(value = "/findReturn",method = {RequestMethod.POST})
    public ResponseEntity findReturn(@RequestBody IdForm form){
        if(form.id==null){
            return  new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        }

        ExpReceiveOrder expReceiveOrder = recieveOrderService.findReturn(form.id);
        JSONObject json = new JSONObject();
        json.put("orderNo",expReceiveOrder.getOrderNo());
        json.put("orderStatus",expReceiveOrder.getOrderStatus());
        json.put("deliveryNo",expReceiveOrder.getDeliveryNo());
        json.put("createDate",DateTimeUtils.parseStr(expReceiveOrder.getCreatedDate()));
        json.put("arrivedDate",DateTimeUtils.parseStr(expReceiveOrder.getArrivedDate()));
        json.put("validateCode",expReceiveOrder.getValidateCode()==null?"":expReceiveOrder.getValidateCode());
        json.put("expressCompany",expReceiveOrder.getExpDeliveryCompany().getName());

        return new ResponseEntity(new RestResponseEntity(100, "成功",json), HttpStatus.OK);
    }

    /**
     * @Description: 快件查询列表
     * @Author: disvenk.dai
     * @Date: 2018/1/18
     */
    @RequestMapping(value = "/findReceiveList",method = {RequestMethod.POST})
    public ResponseEntity findReceiveList(@RequestBody ExpressQueryForm form){
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<ExpReceiveOrder> list = recieveOrderService.findReceiveList(pageQuery,form);
        JSONArray data = new JSONArray();
       if(list!=null && !list.isEmpty()){
           for (ExpReceiveOrder expReceiveOrder:list ) {
               JSONObject json = new JSONObject();
               json.put("id",expReceiveOrder.getId());
               json.put("deliveryNo",expReceiveOrder.getDeliveryNo());
              ExpUser expUser = recieveOrderService.get2(ExpUser.class,"id",expReceiveOrder.getUserId());
               json.put("receiverTel",expUser==null?"":expUser.getUserMobile());
               json.put("orderStatus", Commo.parseReceiveOrderStatus(expReceiveOrder.getOrderStatus()));
               data.add(json);
           }
       }
        return new ResponseEntity(new PageResponseEntity(100, "成功",data, pageQuery.page, pageQuery.limit, list.total), HttpStatus.OK);
    }

    /**
     * @Description: 查询详情
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @RequestMapping(value = "/getReceiveOrder", method = {RequestMethod.POST})
    public ResponseEntity getReceiveOrder(@RequestBody IdForm form) throws Exception {
        ExpReceiveOrder order = recieveOrderService.getReceiveOrder(form.id);
        JSONObject json = new JSONObject();
        json.put("id", order.getId());
        json.put("orderNo", order.getOrderNo());//订单号
        json.put("deliveryNo", order.getDeliveryNo());//快递单号
        json.put("deliveryCompanyId", order.getDeliveryId());
        json.put("deliveryCompanyName", order.getExpDeliveryCompany() == null ? "" : order.getExpDeliveryCompany().getName());//快递公司
        json.put("receiveName",order.getReceiverName()==null?"":order.getReceiverName());//姓名
        ExpUser expUser = recieveOrderService.get2(ExpUser.class,"id",order.getUserId());
        json.put("receiveTel",order.getReceiverTel()==null?"":order.getReceiverTel());//收件人
        json.put("userMobile",expUser.getUserMobile());//用户手机号
        json.put("arrivedDate",order.getArrivedDate()==null?"":DateTimeUtils.parseStr(order.getArrivedDate()));//入库时间
        json.put("orderStatusName", Commo.parseReceiveOrderStatus(order.getOrderStatus()));//状态
        json.put("orderStatus", order.getOrderStatus());//状态编号
        json.put("takeType",order.getType()==null?"":Commo.parseReceiveType(order.getType()));//取件方式
        json.put("validateCode", order.getValidateCode()==null?"":order.getValidateCode());//验证码
        json.put("doorTimeBeginEnd",order.getBeginEndTime()==null?"":order.getBeginEndTime());//上门时间
        json.put("toDoorFee",order.getTodoorFee()==null?"":order.getTodoorFee());//上门小费
        json.put("xiaoGe",order.getExpUserDeliverer()==null?"":order.getExpUserDeliverer().getRelName());//上门人员
        json.put("xiaoGeTel",order.getExpUserDeliverer()==null?"":order.getExpUserDeliverer().getUserMobile());//上门人员电话
        StringBuilder sb = new StringBuilder();
        sb.append(order.getProvince() == null ? "" : order.getProvince());
        sb.append(order.getCity() == null ? "" : order.getCity());
        sb.append(order.getZone() == null ? "" : order.getZone());
        sb.append(order.getReceiverAddress() == null ? "" : order.getReceiverAddress());
        json.put("receiveAddress",sb.toString());//地址
        json.put("remarks", order.getRemarks()==null?"":order.getRemarks());//备注
        json.put("signtTime",order.getExpressDate()==null?"":DateTimeUtils.parseStr(order.getExpressDate()));//签收时间
        json.put("refuseTime",order.getRefuseDate()==null?"":DateTimeUtils.parseStr(order.getRefuseDate()));//拒收时间
        Payment payment = recieveOrderService.getPayment(form.id);
        if(payment!=null){
            json.put("payTime",DateTimeUtils.parseStr(payment.getFinishTime()));
        }else {
            json.put("payTime","");
        }


        json.put("doorTime",order.getTodoorDate()==null?"":DateTimeUtils.parseStr(order.getTodoorDate()));//上门时间

        //下单时间
        json.put("orderDate",order.getCreatedDate()==null?"":DateTimeUtils.parseStr(order.getCreatedDate()));
        //到达时间
        json.put("reachDate", order.getReachDate()==null?"":DateTimeUtils.parseStr(order.getReachDate()));
        //签收时间
        json.put("expressDate",order.getExpressDate()==null?"":DateTimeUtils.parseStr(order.getExpressDate()));
        //拒收时间
        json.put("refuse",order.getOrderStatus()==null? "": DateTimeUtils.parseStr((order.getRefuseDate())));
        //验收码



        //备注

        //支付相关

        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }

    /**
     *@Description: 上门人员自行拒收
     *@Author: disvenk.dai
     *@Date: 上午 10:59 2018/2/23 0023
     */
    @RequestMapping(value = "/refuseByXiaoGe",method = RequestMethod.POST)
    public ResponseEntity refuseByXiaoGe(@RequestBody IdForm form) throws ResponseEntityException, UpsertException {
        if(form.id==null){
            return new ResponseEntity(new RestResponseEntity(110,"id不能为空",null),HttpStatus.OK);
        }
        recieveOrderService.refuseByXiaoGe(form.id);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }
}
