package com.xxx.admin.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.*;
import com.xxx.admin.service.CustomerService;
import com.xxx.admin.service.SendManageService;
import com.xxx.core.query.MybatisPageQuery;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.*;
import com.xxx.user.Commo;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.utils.SessionUtils;
import com.xxx.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


@Controller
@RequestMapping("/send")
public class SendManageController {

    @Autowired
    private SendManageService sendManageService;
    /**
     * @Description:根据网点权限显示快递公司信息
     * @Author: hanchao
     * @Date: 2018/1/22 0022
     */
    @RequestMapping(value = "/list", method = {RequestMethod.POST})
    public ResponseEntity list(HttpServletRequest request) throws Exception {
        List<ExpSchoolDeliveryCompany> list = sendManageService.findList((List<Integer>) SessionUtils.getSession(request,SessionUtils.SCHOOL_ID));
        Set<Object> data = new HashSet<>();
        for (ExpSchoolDeliveryCompany exp : list) {
            JSONObject json = new JSONObject();
            json.put("id", exp.getExpDeliveryCompany() == null ? "" : exp.getExpDeliveryCompany().getId());
            json.put("name", exp.getExpDeliveryCompany() == null ? "" : exp.getExpDeliveryCompany().getName());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }

    /**
     * @Description:对账管理
     * @Author: hanchao
     * @Date: 2018/1/12 0012
     */
    @RequestMapping(value = "/accountManagePageList", method = {RequestMethod.GET})
    public ResponseEntity accountManagePageList(HttpServletRequest request,
                                                  @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  @RequestParam(name = "schoolId", required = false) Integer schoolId)throws Exception {
        if (pageSize >= 100)
            return new ResponseEntity(new RestResponseEntity(110, "条数过长", null), HttpStatus.OK);
        MybatisPageQuery pageQuery = new MybatisPageQuery();
        pageQuery.getParams().put("schoolIdList",(List<Integer>) SessionUtils.getSession(request,SessionUtils.SCHOOL_ID));
        pageQuery.getParams().put("schoolId", schoolId);
        PageList<JSONObject> accountManagePageList = sendManageService.findAccountManagePageList(pageQuery);
        JSONArray data = new JSONArray();
        for (JSONObject jso : accountManagePageList) {
            JSONObject json = new JSONObject();
            json.put("id", jso.get("id"));
            json.put("time", jso.get("time"));
            json.put("deliveryName", jso.get("deliveryName"));
            json.put("count", jso.get("count"));
            json.put("cost", MathUtils.formatObjectReturnDouble(jso.get("cost")));
            json.put("costTotal",MathUtils.formatObjectReturnDouble((jso.get("count") == null ? 0 : Double.parseDouble((jso.get("count")+""))) * ( jso.get("cost") == null ? 0 : Double.parseDouble(jso.get("cost")+""))));
            json.put("price",MathUtils.formatObjectReturnDouble(jso.get("price")));
            json.put("priceTotal",MathUtils.formatObjectReturnDouble((jso.get("count") == null ? 0 : Double.parseDouble((jso.get("count")+""))) * ( jso.get("price") == null ? 0 : Double.parseDouble(jso.get("price")+""))));
            json.put("schoolName", jso.get("schoolName"));
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.getOffset(), pageQuery.getLimit(), accountManagePageList.total), HttpStatus.OK);
    }

    /**
     * @Description:寄件订单删除
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    @RequestMapping(value = "/expSendOrderDelete", method = {RequestMethod.POST})
    public ResponseEntity expSendOrderDelete(@RequestBody IdForm form) throws Exception {
        if(form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不存在", null), HttpStatus.OK);
        if(CurrentUser.get().userId != 1)
            return new ResponseEntity(new RestResponseEntity(120, "非超级管理员无法删除寄件订单", null), HttpStatus.OK);
        sendManageService.expSendOrderDelete(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description:寄件订单编辑保存
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    @RequestMapping(value = "/updateExpSendOrder", method = {RequestMethod.POST})
    public ResponseEntity updateExpSendOrder(@RequestBody ExpSendOrderForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        if (!JavaValidate.isMobileNO(form.receiverTel) && StringUtils.isNotBlank(form.receiverTel))
            return new ResponseEntity(new RestResponseEntity(130, "收件人手机号格式不正确", null), HttpStatus.OK);
        if (!JavaValidate.isMobileNO(form.sendTel) && StringUtils.isNotBlank(form.sendTel))
            return new ResponseEntity(new RestResponseEntity(140, "寄件人手机号格式不正确", null), HttpStatus.OK);
        sendManageService.updateExpSendOrder(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:寄件订单的数据统计
     * @Author: hanchao
     * @Date: 2018/1/26 0026
     */
    @RequestMapping(value = "/expSendOrderCount", method = {RequestMethod.POST})
    public ResponseEntity expReceiveOrderCount(HttpServletRequest request) throws Exception {
        JSONArray data = new JSONArray();
        JSONObject page = new JSONObject();
        Integer todoorQty = 0;/*上门取件数*/
        Integer sendQty = 0;/*自行寄件数*/
        page.put("schoolIdList",(List<Integer>) SessionUtils.getSession(request,SessionUtils.SCHOOL_ID));
            /*获取寄件订单的费用(上门取件)*/
            List<JSONObject> list = sendManageService.findSendOrderCount(page);
            JSONObject json = new JSONObject();
            for(JSONObject jss : list){
                json.put("todoorFeeTotal",MathUtils.formatObjectReturnDouble(jss.get("todoorFeeTotal")));/*上门费用*/
                json.put("todoorPriceTotal", MathUtils.formatObjectReturnDouble(jss.get("priceTotal")));/*快递费*/
                json.put("toodoorCostTotal",MathUtils.formatObjectReturnDouble(jss.get("costTotal")));/*成本*/
                todoorQty = ((Long)(jss.get("todoorQty") == null ? "0" : jss.get("todoorQty"))).intValue();/*上门取件总件数*/
                json.put("todoorQtyTotal",todoorQty);
            }
        /*获取寄件订单的费用(自行寄件)*/
            List<JSONObject> list1 = sendManageService.findSendQtyCount(page);
            for(JSONObject jss : list1){
                json.put("priceTotal",MathUtils.formatObjectReturnDouble(jss.get("priceTotal")));/*快递费*/
                json.put("costTotal",MathUtils.formatObjectReturnDouble(jss.get("costTotal")));/*成本*/
                sendQty = ((Long)(jss.get("todoorQty") == null ? "0" : jss.get("todoorQty"))).intValue();/*自行寄件总件数*/
                json.put("sendQty",sendQty);
            }
            Integer totalQty = todoorQty+sendQty;/*订单数*/
            json.put("totalQty",totalQty);
            data.add(json);
        return new ResponseEntity(new RestResponseEntity(100,"成功", data), HttpStatus.OK);
    }

    /**
     * @Description:寄件订单编辑
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    @RequestMapping(value = "/expSendOrderEdit", method = {RequestMethod.POST})
    public ResponseEntity expSendOrderEdit(@RequestBody IdForm form) throws Exception {
        if(form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        ExpSendOrder exp = sendManageService.get2(ExpSendOrder.class,form.id);
        if(exp == null)
            return new ResponseEntity(new RestResponseEntity(120, "寄件订单信息不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("id", exp.getId());
        json.put("orderNo", exp.getOrderNo());
        json.put("deliveryNo", exp.getDeliveryNo());/*快递单号*/
        json.put("deliveryId",exp.getDeliveryId());
        json.put("deliveryName",exp.getExpDeliveryCompany() == null ? "" : exp.getExpDeliveryCompany().getName());/*快递公司*/
        json.put("userCode",exp.getExpUser() == null ? "" : exp.getExpUser().getUserCode());
        json.put("userMobile",exp.getExpUser() == null ? "" : exp.getExpUser().getUserMobile());
        json.put("price",MathUtils.formatObjectReturnDouble(exp.getPrice()));/*快递费*/
        json.put("todoorFee",MathUtils.formatObjectReturnDouble(exp.getTodoorFee()));/*上门费*/
        json.put("cost",MathUtils.formatObjectReturnDouble(exp.getCost()));/*成本*/
        json.put("payStatus",exp.getPayStatus());/*收费状态*/
        json.put("payStatusName",Commo.parsePayStatus(exp.getPayStatus()));/*收费状态*/
        json.put("payType","");/*支付方式*/
        json.put("payTime","");/*支付时间*/
        Payment pay = sendManageService.get2(Payment.class,"orderId",exp.getId(),"status",20,"orderType",2);
        if(pay != null){
            json.put("payTime",DateTimeUtils.parseStr(pay.getFinishTime()));
            json.put("payType",pay.getChannel());
        }
        json.put("orderTime",DateTimeUtils.parseStr(exp.getCreatedDate()));/*下单时间*/
        json.put("type",exp.getType());/*寄件方式*/
        json.put("typeName",Commo.parseSendType(exp.getType()));/*寄件方式*/
        json.put("orderStatus",exp.getOrderStatus());/*订单状态*/
        json.put("orderStatusName",Commo.parseOrderStatus(exp.getOrderStatus()));/*订单状态*/
        json.put("expressDate",DateTimeUtils.parseStr(exp.getExpressDate()));/*取件时间*/
        json.put("weight",exp.getWeight());/*物品重量*/
        json.put("goodsTypeName",exp.getGoodsTypeName());/*物品类型*/
        json.put("schoolId",exp.getSchoolId());
        json.put("schoolName",exp.getExpSchool() == null ? "" : exp.getExpSchool().getName());/*网点名称*/
        json.put("receiverName",exp.getReceiverName());/*收件人名称*/
        json.put("receiverTel",exp.getReceiverTel());/*收件人电话*/
        json.put("receiverAddress",exp.getReceiverAddress());/*收件人地址*/
        json.put("province",exp.getReceiverProvince());
        if(StringUtils.isNotBlank(exp.getReceiverProvince())){
            ExpArea e = sendManageService.get2(ExpArea.class,"name",exp.getReceiverProvince(),"type",1);
            json.put("provinceCode",e.getCode());
            if(StringUtils.isNotBlank(exp.getReceiverCity())){
                ExpArea e1 = sendManageService.get2(ExpArea.class,"name",exp.getReceiverCity(),"parentCode",e.getCode());
                json.put("cityCode",e1.getCode());
            }
        }
        json.put("city",exp.getReceiverCity());
        json.put("zone",exp.getReceiverZone());
        json.put("sendName",exp.getSendName());/*寄件人名称*/
        json.put("sendTel",exp.getSendTel());/*寄件人电话*/
        json.put("sendAddress",exp.getSendAddress());/*寄件人地址*/
        json.put("sendProvince",exp.getSendProvince());
        if(StringUtils.isNotBlank(exp.getSendProvince())){
            ExpArea e = sendManageService.get2(ExpArea.class,"name",exp.getSendProvince());
            json.put("sendProvinceCode",e.getCode());
            if(StringUtils.isNotBlank(exp.getSendCity())){
                ExpArea e1 = sendManageService.get2(ExpArea.class,"name",exp.getSendCity(),"parentCode",e.getCode());
                json.put("sendCityCode",e1.getCode());
            }
        }
        json.put("sendCity",exp.getSendCity());

        json.put("sendZone",exp.getSendZone());
        json.put("todoorDate",exp.getTodoorBeginEND()==null?"":exp.getTodoorBeginEND());/*上门时间*/
        json.put("delivererId",exp.getDelivererId());/*上门人员*/
        json.put("remark",exp.getRemarks());/*备注*/
        for(String key : json.keySet()){
            if(json.get(key) == null) json.put(key,"");
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功", json), HttpStatus.OK);
    }

    /**
     * @Description:寄件订单列表导出
     * @Author: hanchao
     * @Date: 2018/1/29 0029
     */
    @RequestMapping(value = "/exportExcelPageList", method = {RequestMethod.GET})
    public void getExcel(HttpServletRequest request,HttpServletResponse response,
                         @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                         @RequestParam(name = "userCode", required = false) String userCode,
                         @RequestParam(name = "userMobile", required = false) String userMobile,
                         @RequestParam(name = "type", required = false) Integer type,
                         @RequestParam(name = "orderStatus", required = false) Integer orderStatus,
                         @RequestParam(name = "orderStartTime", required = false) String orderStartTime,
                         @RequestParam(name = "orderEndTime", required = false) String orderEndTime,
                         @RequestParam(name = "isDelete", required = false) Boolean isDelete,
                         @RequestParam(name = "orderNo", required = false) String orderNo,
                         @RequestParam(name = "deliveryNo", required = false) String deliveryNo,
                         @RequestParam(name = "schoolId", required = false) Integer schoolId)throws Exception {
        PageQuery pageQuery = new PageQuery(1);
        pageQuery.limit = 9999;
        if(StringUtils.isNotBlank(userCode)){
            userCode = new String(userCode.getBytes("iso8859-1"),"utf-8");
        }
        if(StringUtils.isNotBlank(userMobile)){
            userMobile = new String(userMobile.getBytes("iso8859-1"),"utf-8");
        }
        if(StringUtils.isNotBlank(orderStartTime)){
            orderStartTime = new String(orderStartTime.getBytes("iso8859-1"),"utf-8");
        }
        if(StringUtils.isNotBlank(orderEndTime)){
            orderEndTime = new String(orderEndTime.getBytes("iso8859-1"),"utf-8");
        }
        List<ExpSendOrder> sendOrderList = sendManageService.findSendOrderList(pageQuery,isDelete,userCode,userMobile,type,orderStatus,orderStartTime,orderEndTime,schoolId,(List<Integer>) SessionUtils.getSession(request,SessionUtils.SCHOOL_ID), orderNo, deliveryNo);
        //导出文件的标题
        String title = "寄件订单列表"+DateTimeUtils.parseStr(new Date(),"yyyy-MM-dd")+".xls";
        //设置表格标题行
        String[] headers = new String[] {"编号","订单号","快递单号", "快递公司","用户名","手机号", "快递费", "上门费","上门小费","成本","收费状态","支付方式", "支付时间","下单时间", "寄件方式", "订单状态", "取件时间", "物品重量", "物品类型", "所属网点", "收件人名称", "收件人电话", "收件人地址","寄件人名称", "寄件人电话", "寄件人地址", "上门时间", "上门人员", "备注"};
        List<Object[]> dataList = new ArrayList<Object[]>();
        Object[] objs = null;
        for (ExpSendOrder exp : sendOrderList) {//循环每一条数据
            objs = new Object[headers.length];
            objs[1] = exp.getOrderNo() == null ? "" : exp.getOrderNo();
            objs[2] = exp.getDeliveryNo() == null ? "" : exp.getDeliveryNo();
            objs[3] = exp.getExpDeliveryCompany() == null ? "" : exp.getExpDeliveryCompany().getName();
            objs[4] = exp.getExpUser() == null ? "" : exp.getExpUser().getUserCode();
            objs[5] = exp.getExpUser() == null ? "" : exp.getExpUser().getUserMobile();
            objs[6] = MathUtils.formatObjectReturnDouble(exp.getPrice()) == null ? "" : MathUtils.formatObjectReturnDouble(exp.getPrice());
            objs[7] = MathUtils.formatObjectReturnDouble(exp.getTodoorFee()) == null ? "" : MathUtils.formatObjectReturnDouble(exp.getTodoorFee());
            objs[8] = MathUtils.formatObjectReturnDouble(exp.getTodoorTipFee()) == null ? "" : MathUtils.formatObjectReturnDouble(exp.getTodoorTipFee());
            objs[9] = MathUtils.formatObjectReturnDouble(exp.getCost()) == null ? "" : MathUtils.formatObjectReturnDouble(exp.getCost());
            objs[10] = Commo.parsePayStatus(exp.getPayStatus()) == null ? "" : Commo.parsePayStatus(exp.getPayStatus());
            Payment pay = sendManageService.get2(Payment.class,"orderId",exp.getId(),"status",20,"orderType",2);
            if(pay != null){
                objs[11] = pay.getChannel() == null ? "" : pay.getChannel();//支付方式
                objs[12] = DateTimeUtils.parseStr(pay.getFinishTime()) == null ? "" : DateTimeUtils.parseStr(pay.getFinishTime());
            }
            objs[13] = DateTimeUtils.parseStr(exp.getCreatedDate()) == null ? "" : DateTimeUtils.parseStr(exp.getCreatedDate());//下单时间
            objs[14] = Commo.parseSendType(exp.getType()) == null ? "" : Commo.parseSendType(exp.getType());
            objs[15] = Commo.parseOrderStatus(exp.getOrderStatus()) == null ? "" : Commo.parseOrderStatus(exp.getOrderStatus());
            objs[16] = DateTimeUtils.parseStr(exp.getExpressDate()) == null ? "" : DateTimeUtils.parseStr(exp.getExpressDate());
            objs[17] = exp.getWeight() == null ? "" : exp.getWeight();
            objs[18] = exp.getGoodsTypeName() == null ? "" : exp.getGoodsTypeName();
            objs[19] = exp.getExpSchool() == null ? "" : exp.getExpSchool().getName();
            objs[20] = exp.getReceiverName() == null ? "" : exp.getReceiverName();
            objs[21] = exp.getReceiverTel() == null ? "" : exp.getReceiverTel();
            StringBuilder sb = new StringBuilder();
            sb.append(exp.getReceiverProvince() == null ? "" : exp.getReceiverProvince());
            sb.append(exp.getReceiverCity() == null ? "" : exp.getReceiverCity());
            sb.append(exp.getReceiverZone() == null ? "" : exp.getReceiverZone());
            sb.append(exp.getReceiverAddress() == null ? "" : exp.getReceiverAddress());
            objs[22] = sb == null ? "" : sb;
            objs[23] = exp.getSendName() == null ? "" : exp.getSendName();
            objs[24] = exp.getSendTel() == null ? "" : exp.getSendTel();
            StringBuilder sb1 = new StringBuilder();
            sb1.append(exp.getSendProvince() == null ? "" : exp.getSendProvince());
            sb1.append(exp.getSendCity() == null ? "" : exp.getSendCity());
            sb1.append(exp.getSendZone() == null ? "" : exp.getSendZone());
            sb1.append(exp.getSendAddress() == null ? "" : exp.getSendAddress());
            objs[25] = sb1 == null ? "" : sb1;
            objs[26] = exp.getTodoorBeginEND() == null ? "" : exp.getTodoorBeginEND();
            if(exp.getDelivererId() != null){
                ExpUser deliverer = sendManageService.get2(ExpUser.class,"id",exp.getDelivererId(),"logicDeleted",false);
                objs[27] = deliverer == null ? "" : deliverer.getUserCode();
            }
            objs[28] = exp.getRemarks() == null ? "" : exp.getRemarks();
            //数据添加到excel表格
            dataList.add(objs);
        }
        ExportExcelUtil.exportExcel(request, response, title, headers, dataList,null);
    }

    /**
     * @Description:寄件订单列表
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    @RequestMapping(value = "/expSendOrderPageList", method = {RequestMethod.GET})
    public ResponseEntity expSendOrderPageList(HttpServletRequest request,
                                                  @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  @RequestParam(name = "userCode", required = false) String userCode,
                                                  @RequestParam(name = "userMobile", required = false) String userMobile,
                                                  @RequestParam(name = "type", required = false) Integer type,
                                                  @RequestParam(name = "orderStatus", required = false) Integer orderStatus,
                                                  @RequestParam(name = "orderStartTime", required = false) String orderStartTime,
                                                  @RequestParam(name = "orderEndTime", required = false) String orderEndTime,
                                                  @RequestParam(name = "isDelete", required = false) Boolean isDelete,
                                                  @RequestParam(name = "orderNo", required = false) String orderNo,
                                                  @RequestParam(name = "deliveryNo", required = false) String deliveryNo,
                                                  @RequestParam(name = "schoolId", required = false) Integer schoolId)throws Exception {
        if (pageSize >= 100)
            return new ResponseEntity(new RestResponseEntity(110, "条数过长", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(pageNum);
        pageQuery.limit = pageSize;
        if(StringUtils.isNotBlank(userCode)){
            userCode = new String(userCode.getBytes("iso8859-1"),"utf-8");
        }
        if(StringUtils.isNotBlank(userMobile)){
            userMobile = new String(userMobile.getBytes("iso8859-1"),"utf-8");
        }
        if(StringUtils.isNotBlank(orderStartTime)){
            orderStartTime = new String(orderStartTime.getBytes("iso8859-1"),"utf-8");
        }
        if(StringUtils.isNotBlank(orderEndTime)){
            orderEndTime = new String(orderEndTime.getBytes("iso8859-1"),"utf-8");
        }
        List<ExpSendOrder> sendOrderList = sendManageService.findSendOrderList(pageQuery,isDelete,userCode,userMobile,type,orderStatus,orderStartTime,orderEndTime,schoolId,(List<Integer>) SessionUtils.getSession(request,SessionUtils.SCHOOL_ID),
                orderNo, deliveryNo);
        JSONArray data = new JSONArray();
        for (ExpSendOrder exp : sendOrderList) {
            JSONObject json = new JSONObject();
            json.put("id", exp.getId());
            json.put("orderNo", exp.getOrderNo());
            json.put("deliveryNo", exp.getDeliveryNo());/*快递单号*/
            json.put("deliveryId",exp.getDeliveryId());
            json.put("deliveryName",exp.getExpDeliveryCompany() == null ? "" : exp.getExpDeliveryCompany().getName());/*快递公司*/
            ExpUser expUser = sendManageService.get2(ExpUser.class,exp.getUserId());
            json.put("userCode",expUser == null ? "" : expUser.getUserCode());
            json.put("userMobile",expUser == null ? "" : expUser.getUserMobile());
            json.put("price",MathUtils.formatObjectReturnDouble(exp.getPrice()));/*快递费*/
            json.put("todoorFee",MathUtils.formatObjectReturnDouble(exp.getTodoorFee()));/*上门费*/
            json.put("todoorTipFee",MathUtils.formatObjectReturnDouble(exp.getTodoorTipFee()));/*上门小费*/
            json.put("cost",MathUtils.formatObjectReturnDouble(exp.getCost()));/*成本*/
            json.put("payStatus",exp.getPayStatus());/*收费状态*/
            json.put("payStatusName",Commo.parsePayStatus(exp.getPayStatus()));/*收费状态*/
            json.put("payType","");/*支付方式*/
            json.put("payTime","");/*支付时间*/
            Payment pay = sendManageService.get2(Payment.class,"orderId",exp.getId(),"status",20,"orderType",2);
            if(pay != null){
                json.put("payTime",DateTimeUtils.parseStr(pay.getFinishTime()));
                json.put("payType",pay.getChannel());
            }
            json.put("orderTime",DateTimeUtils.parseStr(exp.getCreatedDate()));/*下单时间*/
            json.put("type",exp.getType());/*寄件方式*/
            json.put("typeName",Commo.parseSendType(exp.getType()));/*寄件方式*/
            json.put("orderStatus",exp.getOrderStatus());/*订单状态*/
            json.put("orderStatusName",Commo.parseOrderStatus(exp.getOrderStatus()));/*订单状态*/
            json.put("expressDate",DateTimeUtils.parseStr(exp.getExpressDate()));/*取件时间*/
            json.put("goodsTypeId",exp.getGoodsTypeId());/*物品类型id*/
            json.put("weight",exp.getWeight());/*物品重量*/
            json.put("goodsTypeName",exp.getGoodsTypeName());/*物品类型*/
            json.put("schoolId",exp.getSchoolId());
            json.put("schoolName",exp.getExpSchool() == null ? "" : exp.getExpSchool().getName());/*网点名称*/
            json.put("receiverName",exp.getReceiverName());/*收件人名称*/
            json.put("receiverTel",exp.getReceiverTel());/*收件人电话*/
//            json.put("receiverAddress",exp.getReceiverAddress());/*收件人地址*/
            StringBuilder sb = new StringBuilder();
            sb.append(exp.getReceiverProvince() == null ? "" : exp.getReceiverProvince());
            sb.append(exp.getReceiverCity() == null ? "" : exp.getReceiverCity());
            sb.append(exp.getReceiverZone() == null ? "" : exp.getReceiverZone());
            sb.append(exp.getReceiverAddress() == null ? "" : exp.getReceiverAddress());
            json.put("receiverAddress",sb);
            StringBuilder sb1 = new StringBuilder();
            sb1.append(exp.getSendProvince() == null ? "" : exp.getSendProvince());
            sb1.append(exp.getSendCity() == null ? "" : exp.getSendCity());
            sb1.append(exp.getSendZone() == null ? "" : exp.getSendZone());
            sb1.append(exp.getSendAddress() == null ? "" : exp.getSendAddress());
            json.put("sendAddress",sb1);/*寄件人地址*/
            json.put("sendName",exp.getSendName());/*寄件人名称*/
            json.put("sendTel",exp.getSendTel());/*寄件人电话*/
            json.put("todoorDate",exp.getTodoorBeginEND() == null ? "" : exp.getTodoorBeginEND());/*上门时间*/
            if(exp.getDelivererId() != null) {
                ExpUser deliverer = sendManageService.get2(ExpUser.class,"id",exp.getDelivererId(),"logicDeleted",false);
                json.put("delivererId",exp.getDelivererId());/*上门人员*/
                json.put("delivererName",deliverer == null ? "" : deliverer.getUserCode());/*上门人员*/
            }
            json.put("remark",exp.getRemarks());/*备注*/
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.page, pageQuery.limit, pageQuery.total), HttpStatus.OK);
    }

    /**
     * @Description:快递单批量删除
     * @Author: hanchao
     * @Date: 2018/1/6 0006
     */
    @RequestMapping(value = "/deliveryOrdernoDeleteIds", method = {RequestMethod.POST})
    public ResponseEntity deliveryOrdernoDeleteIds(@RequestBody IdsForm form) throws Exception {
        if(form.ids.size() == 0)
            return new ResponseEntity(new RestResponseEntity(110, "请选择要删除的项", null), HttpStatus.OK);
        sendManageService.deliveryOrdernoDeleteIds(form.ids);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description:快递单删除
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    @RequestMapping(value = "/deliveryOrdernoDelete", method = {RequestMethod.POST})
    public ResponseEntity deliveryOrdernoDelete(@RequestBody IdForm form) throws Exception {
        if(form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不存在", null), HttpStatus.OK);
        sendManageService.deliveryOrdernoDelete(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description:快递单新增
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    @RequestMapping(value = "/addDeliveryOrderno", method = {RequestMethod.POST})
    public ResponseEntity addDeliveryOrderno(@RequestBody DeliveryOrdernoForm form) throws Exception {
        sendManageService.addDeliveryOrderno(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:快递单维护列表
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    @RequestMapping(value = "/deliveryOrderNoPageList", method = {RequestMethod.GET})
    public ResponseEntity deliveryOrderNoPageList(HttpServletRequest request,
                                             @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                             @RequestParam(name = "deliveryNo", required = false) String deliveryNo,
                                             @RequestParam(name = "deliveryId", required = false) Integer deliveryId,
                                             @RequestParam(name = "isUsed", required = false) Boolean isUsed,
                                             @RequestParam(name = "schoolId", required = false) Integer schoolId)throws Exception {
        if (pageSize >= 100)
            return new ResponseEntity(new RestResponseEntity(110, "条数过长", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(pageNum);
        pageQuery.limit = pageSize;
        if(StringUtils.isNotBlank(deliveryNo)){
            deliveryNo = new String(deliveryNo.getBytes("iso8859-1"),"utf-8");
        }
        List<ExpDeliveryOrderNo> deliveryOrderNoList = sendManageService.findDeliveryOrderNoPageList(pageQuery,deliveryNo,deliveryId,isUsed,schoolId,(List<Integer>) SessionUtils.getSession(request,SessionUtils.SCHOOL_ID));
        JSONArray list = new JSONArray();
        for (ExpDeliveryOrderNo exp : deliveryOrderNoList) {
            JSONObject json = new JSONObject();
            json.put("id", exp.getId());
            json.put("deliveryNo",exp.getDeliveryNo());
            json.put("deliveryId",exp.getDeliveryId());
            json.put("deliveryName",exp.getExpDeliveryCompany() == null ? "" : exp.getExpDeliveryCompany().getName());
            json.put("isUsed",exp.getUsed() == true ? "1" : "0");
            json.put("isUsedName",exp.getUsed() == true ? "已使用" : "未使用");
            json.put("schoolId",exp.getSchoolId());
            json.put("schoolName",exp.getExpSchool() == null ? "" : exp.getExpSchool().getName());
            list.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", list, pageQuery.page, pageQuery.limit, pageQuery.total), HttpStatus.OK);
    }


    /**
     * @Description:快递费删除
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    @RequestMapping(value = "/deliveryProvinceServiceFeeDelete", method = {RequestMethod.POST})
    public ResponseEntity deliveryProvinceServiceFeeDelete(@RequestBody IdForm form) throws Exception {
        if(form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不存在", null), HttpStatus.OK);
        sendManageService.deliveryProvinceServiceFeeDelete(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description:快递费维护单元格编辑保存新增
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    @RequestMapping(value = "/addProvinceServiceFee", method = {RequestMethod.POST})
    public ResponseEntity addProvinceServiceFee(@RequestBody ExpDeliveryCompanyListForm form) throws Exception {
        if(form.deliveryId == null){
            return new ResponseEntity(new RestResponseEntity(120, "请先选择快递公司", null), HttpStatus.OK);
        }
        if(form.schoolId == null){
            return new ResponseEntity(new RestResponseEntity(130, "请先选择网点", null), HttpStatus.OK);
        }
        sendManageService.addProvinceServiceFee(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:快递费维护提交保存新增
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    @RequestMapping(value = "/addProvinceServiceFeeSubmit", method = {RequestMethod.POST})
    public ResponseEntity addProvinceServiceFeeSubmit(@RequestBody ExpDeliveryCompanyListForm form) throws Exception {
        if(form.deliveryId == null){
            return new ResponseEntity(new RestResponseEntity(120, "请先选择快递公司", null), HttpStatus.OK);
        }
        if(form.schoolId == null){
            return new ResponseEntity(new RestResponseEntity(130, "请先选择网点", null), HttpStatus.OK);
        }
        ExpSchoolDeliveryCompany exp = sendManageService.selectAddDeliveryCompany(form.deliveryId,form.schoolId);
        if(exp != null)
            return new ResponseEntity(new RestResponseEntity(110, "当前网点已维护该快递公司", null), HttpStatus.OK);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:快递费新增列表
     * @Author: hanchao
     * @Date: 2018/1/15 0015
     */
    @RequestMapping(value = "/addProvinceServiceFeePageList", method = {RequestMethod.GET})
    public ResponseEntity addProvinceServiceFeePageList(HttpServletRequest request,
                                                  @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize)throws Exception {
        if (pageSize >= 100)
            return new ResponseEntity(new RestResponseEntity(110, "条数过长", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(pageNum);
        pageQuery.limit = pageSize;
        List<ExpArea> expAreaList = sendManageService.findAddProvinceServiceFeePageList(pageQuery);
        JSONArray list = new JSONArray();
        for (ExpArea exp : expAreaList) {
            JSONObject json = new JSONObject();
            json.put("id", exp.getId());
            json.put("name", exp.getName());
            list.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", list, pageQuery.page, pageQuery.limit, pageQuery.total), HttpStatus.OK);
    }

    /**
     * @Description:快递费维护编辑保存
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    @RequestMapping(value = "/updateProvinceServiceFee", method = {RequestMethod.POST})
    public ResponseEntity updateProvinceServiceFee(@RequestBody ExpDeliveryCompanyListForm form) throws Exception {
        if(form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        ExpSchoolDeliveryCompany exp = sendManageService.selectDeliveryCompany(form.id,form.deliveryId,form.schoolId);
        if(exp != null)
            return new ResponseEntity(new RestResponseEntity(120, "当前网点已维护该快递公司", null), HttpStatus.OK);
        sendManageService.updateProvinceServiceFee(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:快递费维护编辑根据快递id,网点id获取列表
     * @Author: hanchao
     * @Date: 2018/1/17 0017
     */
    @RequestMapping(value = "/provinceServiceFeeEditBySchoolPageList", method = {RequestMethod.GET})
    public ResponseEntity provinceServiceFeeEditBySchoolPageList(HttpServletRequest request,
                                                 @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                 @RequestParam(name = "deliveryId") Integer deliveryId,
                                                 @RequestParam(name = "schoolId") Integer schoolId)throws Exception {
        if(deliveryId == null){
            return new ResponseEntity(new RestResponseEntity(120, "请先选择快递公司", null), HttpStatus.OK);
        }
        if(schoolId == null){
            return new ResponseEntity(new RestResponseEntity(130, "请先选择网点", null), HttpStatus.OK);
        }
        if (pageSize >= 100)
            return new ResponseEntity(new RestResponseEntity(110, "条数过长", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(pageNum);
        pageQuery.limit = pageSize;
        List<ExpArea> expAreaList = sendManageService.findAddProvinceServiceFeePageList(pageQuery);
        ExpSchoolDeliveryCompany expFee = sendManageService.get2(ExpSchoolDeliveryCompany.class,"deliveryId",deliveryId,"schoolId",schoolId,"logicDeleted",false);
        JSONArray data = new JSONArray();
        if(expFee != null){
            for (ExpArea area : expAreaList) {
                ExpProvinceServiceFee exp = sendManageService.get2(ExpProvinceServiceFee.class,"provinceId",area.getId(),"schoolDeliveryCompanyId",expFee.getId(),"logicDeleted",false);
                if(exp != null){
                    JSONObject json = new JSONObject();
                    json.put("id",exp.getExpSchoolDeliveryCompany() == null ? "" : exp.getExpSchoolDeliveryCompany().getId());
                    json.put("schoolId",exp.getExpSchoolDeliveryCompany() == null ? "" : exp.getExpSchoolDeliveryCompany().getSchoolId());
                    json.put("deliveryId",exp.getExpSchoolDeliveryCompany() == null ? "" : exp.getExpSchoolDeliveryCompany().getDeliveryId());
                    json.put("expProviceServiceId",exp.getId());
                    json.put("schoolDeliveryCompanyId",exp.getSchoolDeliveryCompanyId());
                    json.put("provinceName",exp.getProvinceName());
                    json.put("provinceId",exp.getProvinceId());
                    json.put("firstWeightFee",exp.getFirstWeightFee());
                    json.put("otherWeightFee",exp.getOtherWeightFee());
                    json.put("firstCostFee",exp.getFirstCostFee());
                    json.put("otherCostFee",exp.getOtherCostFee());
                    data.add(json);
                }else{
                    JSONObject json1 = new JSONObject();
                    json1.put("provinceId", area.getId());
                    json1.put("provinceName", area.getName());
                    json1.put("schoolDeliveryCompanyId",expFee.getId());
                    json1.put("schoolId",expFee.getSchoolId());
                    json1.put("deliveryId",expFee.getDeliveryId());
                    data.add(json1);
                }
            }
            return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.page, pageQuery.limit, pageQuery.total), HttpStatus.OK);
        }else{
            for (ExpArea area : expAreaList) {
                JSONObject json1 = new JSONObject();
                json1.put("provinceId", area.getId());
                json1.put("provinceName", area.getName());
                data.add(json1);
            }
            return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.page, pageQuery.limit, pageQuery.total), HttpStatus.OK);
        }
    }

    /**
     * @Description:快递费维护编辑列表
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    @RequestMapping(value = "/provinceServiceFeeEditPageList", method = {RequestMethod.GET})
    public ResponseEntity provinceServiceFeeEdit(HttpServletRequest request,
                                                 @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                 @RequestParam(name = "id") Integer id)throws Exception {
        if(id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        if (pageSize >= 100)
            return new ResponseEntity(new RestResponseEntity(110, "条数过长", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(pageNum);
        pageQuery.limit = pageSize;
        List<ExpArea> expAreaList = sendManageService.findAddProvinceServiceFeePageList(pageQuery);
        JSONArray data = new JSONArray();
        for (ExpArea area : expAreaList) {
            ExpProvinceServiceFee exp = sendManageService.get2(ExpProvinceServiceFee.class,"provinceId",area.getId(),"schoolDeliveryCompanyId",id,"logicDeleted",false);
            ExpSchoolDeliveryCompany expFee = sendManageService.get2(ExpSchoolDeliveryCompany.class,"id",id,"logicDeleted",false);
            if(exp != null){
                    JSONObject json = new JSONObject();
                    json.put("id",exp.getExpSchoolDeliveryCompany() == null ? "" : exp.getExpSchoolDeliveryCompany().getId());
                    json.put("schoolId",exp.getExpSchoolDeliveryCompany() == null ? "" : exp.getExpSchoolDeliveryCompany().getSchoolId());
                    json.put("deliveryId",exp.getExpSchoolDeliveryCompany() == null ? "" : exp.getExpSchoolDeliveryCompany().getDeliveryId());
                    json.put("expProviceServiceId",exp.getId());
                    json.put("schoolDeliveryCompanyId",exp.getSchoolDeliveryCompanyId());
                    json.put("provinceName",exp.getProvinceName());
                    json.put("provinceId",exp.getProvinceId());
                    json.put("firstWeightFee",exp.getFirstWeightFee());
                    json.put("otherWeightFee",exp.getOtherWeightFee());
                    json.put("firstCostFee",exp.getFirstCostFee());
                    json.put("otherCostFee",exp.getOtherCostFee());
                    data.add(json);
        }else{
                JSONObject json1 = new JSONObject();
                json1.put("provinceId", area.getId());
                json1.put("provinceName", area.getName());
                json1.put("schoolDeliveryCompanyId",expFee.getId());
                json1.put("schoolId",expFee.getSchoolId());
                json1.put("deliveryId",expFee.getDeliveryId());
                data.add(json1);
            }
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.page, pageQuery.limit, pageQuery.total), HttpStatus.OK);
    }

    /**
     * @Description:快递费维护列表
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    @RequestMapping(value = "/provinceServiceFeePageList", method = {RequestMethod.GET})
    public ResponseEntity provinceServiceFeePageList(HttpServletRequest request,
                                             @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                             @RequestParam(name = "deliveryId", required = false) Integer deliveryId,
                                             @RequestParam(name = "schoolId", required = false) Integer schoolId)throws Exception {
        if (pageSize >= 100)
            return new ResponseEntity(new RestResponseEntity(110, "条数过长", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(pageNum);
        pageQuery.limit = pageSize;
        List<ExpSchoolDeliveryCompany> proviceServiceFeeList = sendManageService.findProvinceServiceFeePageList(pageQuery,deliveryId,schoolId,(List<Integer>) SessionUtils.getSession(request,SessionUtils.SCHOOL_ID));
        JSONArray list = new JSONArray();
        for (ExpSchoolDeliveryCompany exp : proviceServiceFeeList) {
            JSONObject json = new JSONObject();
            json.put("id", exp.getId());
            json.put("deliveryId",exp.getDeliveryId());
            json.put("deliveryName",exp.getExpDeliveryCompany() == null ? "" : exp.getExpDeliveryCompany().getName());
            json.put("schoolId",exp.getSchoolId());
            json.put("schoolName",exp.getExpSchool() == null ? "" :  exp.getExpSchool().getName());
            list.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", list, pageQuery.page, pageQuery.limit, pageQuery.total), HttpStatus.OK);
    }

    /**
     * @Description:快递费删除
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    @RequestMapping(value = "/serviceFeeDelete", method = {RequestMethod.POST})
    public ResponseEntity serviceFeeDelete(@RequestBody IdForm form) throws Exception {
        if(form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不存在", null), HttpStatus.OK);
        sendManageService.serviceFeeDelete(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description:上门费新增
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    @RequestMapping(value = "/addServiceFee", method = {RequestMethod.POST})
    public ResponseEntity addServiceFee(@RequestBody ExpServiceFeeForm form) throws Exception {
        ExpServiceFee exp = sendManageService.selectAddServiceFee(form.type,form.schoolId);
        if(exp != null)
            return new ResponseEntity(new RestResponseEntity(110, "当前网点已维护该上门费", null), HttpStatus.OK);
        sendManageService.addServiceFee(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:上门费编辑保存
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    @RequestMapping(value = "/updateServiceFee", method = {RequestMethod.POST})
    public ResponseEntity updateServiceFee(@RequestBody ExpServiceFeeForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        ExpServiceFee exp = sendManageService.selectServiceFee(form.id,form.type,form.schoolId);
        if(exp != null)
            return new ResponseEntity(new RestResponseEntity(120, "当前网点已维护该上门费", null), HttpStatus.OK);
        sendManageService.updateServiceFee(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:上门费维护编辑
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    @RequestMapping(value = "/serviceFeeEdit", method = {RequestMethod.POST})
    public ResponseEntity serviceFeeEdit(@RequestBody IdForm form) throws Exception {
        if(form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        ExpServiceFee exp = sendManageService.get2(ExpServiceFee.class,form.id);
        if(exp == null)
            return new ResponseEntity(new RestResponseEntity(120, "当前维护的上门费信息不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("id", exp.getId());
        json.put("type",exp.getType());
        json.put("typeName", Commo.parseTodoorype(exp.getType()));
        json.put("fee",exp.getFee());
        json.put("schoolId",exp.getSchoolId());
        json.put("schoolName",exp.getExpSchool() == null ? "" : exp.getExpSchool().getName());
        return new ResponseEntity(new RestResponseEntity(100,"成功", json), HttpStatus.OK);
    }

    /**
     * @Description:上门费维护列表
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    @RequestMapping(value = "/serviceFeePageList", method = {RequestMethod.GET})
    public ResponseEntity serviceFeePageList(HttpServletRequest request,
                                             @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                             @RequestParam(name = "schoolId", required = false) Integer schoolId)throws Exception {
        if (pageSize >= 100)
            return new ResponseEntity(new RestResponseEntity(110, "条数过长", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(pageNum);
        pageQuery.limit = pageSize;
        List<ExpServiceFee> serviceFeeList = sendManageService.findServiceFeePageList(pageQuery,schoolId,(List<Integer>) SessionUtils.getSession(request,SessionUtils.SCHOOL_ID));
        JSONArray list = new JSONArray();
        for (ExpServiceFee exp : serviceFeeList) {
            JSONObject json = new JSONObject();
            json.put("id", exp.getId());
            json.put("type",exp.getType());
            json.put("typeName", Commo.parseTodoorype(exp.getType()));
            json.put("fee",exp.getFee());
            json.put("schoolId",exp.getSchoolId());
            json.put("schoolName",exp.getExpSchool() == null ? "" : exp.getExpSchool().getName());
            list.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", list, pageQuery.page, pageQuery.limit, pageQuery.total), HttpStatus.OK);
    }

    /**
     * @Description:快递公司新增
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    @RequestMapping(value = "/addDeliveryCompany", method = {RequestMethod.POST})
    public ResponseEntity addDeliveryCompany(@RequestBody ExpDeliveryCompanyForm form) throws Exception {
        ExpSchoolDeliveryCompany exp = sendManageService.selectAddDeliveryCompany(form.deliveryId,form.schoolId);
        if(exp != null)
            return new ResponseEntity(new RestResponseEntity(110, "当前网点已维护该快递公司", null), HttpStatus.OK);
        sendManageService.addDeliveryCompany(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

   /**
    * @Description:快递公司编辑保存
    * @Author: hanchao
    * @Date: 2018/1/4 0004
    */
    @RequestMapping(value = "/updateDeliveryCompany", method = {RequestMethod.POST})
    public ResponseEntity updateDeliveryCompany(@RequestBody ExpDeliveryCompanyForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "客户id不能为空", null), HttpStatus.OK);
        ExpSchoolDeliveryCompany exp = sendManageService.selectDeliveryCompany(form.id,form.deliveryId,form.schoolId);
        if(exp != null)
            return new ResponseEntity(new RestResponseEntity(120, "当前网点已维护该快递公司", null), HttpStatus.OK);
        sendManageService.updateDeliveryCompany(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:快递公司编辑
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    @RequestMapping(value = "/deliveryCompanyEdit", method = {RequestMethod.POST})
    public ResponseEntity customerEdit(@RequestBody IdForm form) throws Exception {
        if(form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "快递公司id不存在", null), HttpStatus.OK);
        ExpSchoolDeliveryCompany exp = sendManageService.get2(ExpSchoolDeliveryCompany.class,form.id);
        if(exp == null)
            return new ResponseEntity(new RestResponseEntity(120, "快递公司信息不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("id", exp.getId());
        json.put("deliveryId", exp.getDeliveryId());
        json.put("name",exp.getExpDeliveryCompany() == null ? "" : exp.getExpDeliveryCompany().getName());
        json.put("schoolId",exp.getSchoolId());
        json.put("schoolName",exp.getExpSchool() == null ? "" : exp.getExpSchool().getName());
        return new ResponseEntity(new RestResponseEntity(100,"成功", json), HttpStatus.OK);
    }

    /**
     * @Description:快递公司逻辑删除
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    @RequestMapping(value = "/deliveryCompanyDelete", method = {RequestMethod.POST})
    public ResponseEntity deliveryCompanyDelete(@RequestBody IdForm form) throws Exception {
        if(form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "快递公司id不存在", null), HttpStatus.OK);
        sendManageService.deliveryCompanyDelete(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description:快递公司维护列表
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    @RequestMapping(value = "/deliveryCompanyPageList", method = {RequestMethod.GET})
    public ResponseEntity deliveryCompanyPageList(HttpServletRequest request,
                                           @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                           @RequestParam(name = "schoolId", required = false) Integer schoolId)throws Exception {
        if (pageSize >= 100)
            return new ResponseEntity(new RestResponseEntity(110, "条数过长", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(pageNum);
        pageQuery.limit = pageSize;
        List<ExpSchoolDeliveryCompany> schoolDeliveryList = sendManageService.findSendManagePageList(pageQuery,schoolId,(List<Integer>) SessionUtils.getSession(request,SessionUtils.SCHOOL_ID));
        JSONArray list = new JSONArray();
        for (ExpSchoolDeliveryCompany exp : schoolDeliveryList) {
            JSONObject json = new JSONObject();
            json.put("id", exp.getId());
            json.put("name",exp.getExpDeliveryCompany() == null ? "" : exp.getExpDeliveryCompany().getName());
            json.put("schoolId",exp.getSchoolId());
            json.put("schoolName",exp.getExpSchool() == null ? "" : exp.getExpSchool().getName());
            list.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", list, pageQuery.page, pageQuery.limit, pageQuery.total), HttpStatus.OK);
    }

}
