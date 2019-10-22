package com.xxx.admin.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.*;
import com.xxx.admin.service.ReceiveManageService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/receive")
public class ReceiveManageController {

    @Autowired
    private ReceiveManageService receiveManageService;

    /**
     * @Description:收件订单上门人员变更
     * @Author: hanchao
     * @Date: 2018/1/6 0006
     */
    @RequestMapping(value = "/updateTodoorManageChange", method = {RequestMethod.POST})
    public ResponseEntity updateTodoorManageChange(@RequestBody TodoorManageForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        receiveManageService.updateTodoorManageChange(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:推送成功后修改推送结果
     * @Author: hanchao
     * @Date: 2018/1/25 0025
     */
    @RequestMapping(value = "/updateExpNoticeMessage", method = {RequestMethod.POST})
    public ResponseEntity updateExpNoticeMessage(@RequestBody ExpNoticeMessageForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        receiveManageService.updateExpNoticeMessage(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:推送历史列表
     * @Author: hanchao
     * @Date: 2018/1/6 0006
     */
    @RequestMapping(value = "/expNoticeMessagePageList", method = {RequestMethod.GET})
    public ResponseEntity expNoticeMessagePageList(HttpServletRequest request,
                                               @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                               @RequestParam(name = "userCode", required = false) String userCode,
                                               @RequestParam(name = "userMobile", required = false) String userMobile,
                                               @RequestParam(name = "schoolId", required = false) Integer schoolId)throws Exception {
        if (pageSize >= 100)
            return new ResponseEntity(new RestResponseEntity(110, "条数过长", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(pageNum);
        pageQuery.limit = pageSize;
        if(StringUtils.isNotBlank(userCode)){
            userCode = new String(userCode.getBytes("iso8859-1"),"utf-8");
        }
        List<ExpNoticeMessageLog> expNoticeMessagePageList = receiveManageService.expNoticeMessagePageList(pageQuery,userCode,userMobile,schoolId,(List<Integer>) SessionUtils.getSession(request, SessionUtils.SCHOOL_ID));
        JSONArray list = new JSONArray();
        for (ExpNoticeMessageLog exp : expNoticeMessagePageList) {
            JSONObject json = new JSONObject();
            json.put("id",exp.getId());
            json.put("deliverId",exp.getDeliveryId());
            json.put("deliveryNo",exp.getDeliveryNo());
            json.put("deliverName",exp.getExpDeliveryCompany() == null ? "" : exp.getExpDeliveryCompany().getName());
            json.put("sendTime",DateTimeUtils.parseStr(exp.getCreatedDate()));
            json.put("validateCode",exp.getValidateCode());
            json.put("userCode",exp.getExpUser() == null ? "" : exp.getExpUser().getUserCode());
            json.put("userId",exp.getUserId());
            json.put("userMobile",exp.getUserMobile());
            json.put("sendContent",exp.getSendContent());
            json.put("schoolId",exp.getSchoolId());
            json.put("schoolName",exp.getExpSchool() == null ? "" : exp.getExpSchool().getName());
            json.put("sendResult",exp.getSendResult());
            list.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", list, pageQuery.page, pageQuery.limit, pageQuery.total), HttpStatus.OK);
    }

    /**
     * @Description:短信历史列表
     * @Author: hanchao
     * @Date: 2018/1/6 0006
     */
    @RequestMapping(value = "/expSmsMessagePageList", method = {RequestMethod.GET})
    public ResponseEntity expSmsMessagePageList(HttpServletRequest request,
                                               @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                               @RequestParam(name = "userCode", required = false) String userCode,
                                               @RequestParam(name = "userMobile", required = false) String userMobile,
                                               @RequestParam(name = "schoolId", required = false) Integer schoolId)throws Exception {
        if (pageSize >= 100)
            return new ResponseEntity(new RestResponseEntity(110, "条数过长", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(pageNum);
        pageQuery.limit = pageSize;
        List<ExpSmsMessageLog> expSmsMessagePageList = receiveManageService.expSmsMessagePageList(pageQuery,userCode,userMobile,schoolId,(List<Integer>) SessionUtils.getSession(request,SessionUtils.SCHOOL_ID));
        JSONArray list = new JSONArray();
        for (ExpSmsMessageLog exp : expSmsMessagePageList) {
            JSONObject json = new JSONObject();
            json.put("id",exp.getId());
            json.put("sendTime",DateTimeUtils.parseStr(exp.getCreatedDate()));
            json.put("validateCode",exp.getValidateCode());
            json.put("userMobile",exp.getUserMobile());
            json.put("sendContent",exp.getSendContent());
            json.put("schoolId", exp.getExpUser() == null ? "" : exp.getExpUser().getSchoolId());
            json.put("schoolName",exp.getExpUser() == null ? "" : exp.getExpUser().getExpSchool() == null ? "" :
                    exp.getExpUser().getExpSchool().getName());
            json.put("sendResult",exp.getSendResult());
            list.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", list, pageQuery.page, pageQuery.limit, pageQuery.total), HttpStatus.OK);
    }

    /**
     * @Description:上门人员删除
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    @RequestMapping(value = "/todoorManageDelete", method = {RequestMethod.POST})
    public ResponseEntity todoorManageDelete(@RequestBody IdForm form) throws Exception {
        if(form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "上门人员id不存在", null), HttpStatus.OK);
        receiveManageService.todoorManageDelete(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description:上门人员新增
     * @Author: hanchao
     * @Date: 2018/1/22 0022
     */
    @RequestMapping(value = "/addTodoorManage", method = {RequestMethod.POST})
    public ResponseEntity addTodoorManage(@RequestBody TodoorManageForm form) throws Exception {
        if (form.schoolId == null)
            return new ResponseEntity(new RestResponseEntity(110, "网点不能为空", null), HttpStatus.OK);
        if( StringUtils.isBlank(form.userCode))
            return new ResponseEntity(new RestResponseEntity(120, "用户名不能为空", null), HttpStatus.OK);
        if (!JavaValidate.isMobileNO(form.userMobile) || StringUtils.isBlank(form.userMobile))
            return new ResponseEntity(new RestResponseEntity(130, "手机号格式不正确", null), HttpStatus.OK);
        ExpUser exp = receiveManageService.selectIsTodoorAdd(form.schoolId,form.userMobile);
        if(exp != null){
            return new ResponseEntity(new RestResponseEntity(140, "该用户信息在当前网点已存在", null), HttpStatus.OK);
        }
        receiveManageService.addTodoorManage(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:上门人员编辑保存
     * @Author: hanchao
     * @Date: 2018/1/6 0006
     */
    @RequestMapping(value = "/updateTodoorManage", method = {RequestMethod.POST})
    public ResponseEntity updateTodoorManage(@RequestBody TodoorManageForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        if (form.schoolId == null)
            return new ResponseEntity(new RestResponseEntity(110, "网点不能为空", null), HttpStatus.OK);
        if( StringUtils.isBlank(form.userCode))
            return new ResponseEntity(new RestResponseEntity(120, "用户名不能为空", null), HttpStatus.OK);
        if (!JavaValidate.isMobileNO(form.userMobile) || StringUtils.isBlank(form.userMobile))
            return new ResponseEntity(new RestResponseEntity(130, "手机号格式不正确", null), HttpStatus.OK);
        ExpUser exp = receiveManageService.selectIsTodoor(form.id,form.schoolId,form.userMobile);
        if(exp != null){
            return new ResponseEntity(new RestResponseEntity(140, "该用户信息在当前网点已存在", null), HttpStatus.OK);
        }
        receiveManageService.updateTodoorManage(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:上门人员编辑
     * @Author: hanchao
     * @Date: 2018/1/6 0006
     */
    @RequestMapping(value = "/todoorManageEdit", method = {RequestMethod.POST})
    public ResponseEntity todoorManageEdit(@RequestBody IdForm form) throws Exception {
        if(form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "上门人员id不能为空", null), HttpStatus.OK);
        ExpUser exp = receiveManageService.get2(ExpUser.class,"id",form.id,"logicDeleted",false);
        if(exp == null)
            return new ResponseEntity(new RestResponseEntity(110, "上门人员信息不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("id",exp.getId());
        json.put("userCode",exp.getUserCode());
        json.put("userMobile",exp.getUserMobile());
        json.put("schoolId",exp.getSchoolId());
        json.put("password",exp.getUserPassword());
        json.put("schoolName",exp.getExpSchool() == null ? "" : exp.getExpSchool().getName());
        json.put("remark",exp.getRemarks());
        return new ResponseEntity(new RestResponseEntity(100,"成功", json), HttpStatus.OK);
    }

    /**
     * @Description:上门人员维护列表
     * @Author: hanchao
     * @Date: 2018/1/6 0006
     */
    @RequestMapping(value = "/todoorManagePageList", method = {RequestMethod.GET})
    public ResponseEntity todoorManagePageList(HttpServletRequest request,
                                                @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                @RequestParam(name = "userCode", required = false) String userCode,
                                                @RequestParam(name = "userMobile", required = false) String userMobile,
                                                @RequestParam(name = "schoolId", required = false) Integer schoolId)throws Exception {
        if (pageSize >= 100)
            return new ResponseEntity(new RestResponseEntity(110, "条数过长", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(pageNum);
        pageQuery.limit = pageSize;
        if(StringUtils.isNotBlank(userCode)){
            userCode = new String(userCode.getBytes("iso8859-1"),"utf-8");
        }
        List<ExpUser> arriveMessagePageList = receiveManageService.findTodoorManagePageList(pageQuery,userCode,userMobile,schoolId,(List<Integer>) SessionUtils.getSession(request,SessionUtils.SCHOOL_ID));
        JSONArray list = new JSONArray();
        for (ExpUser exp : arriveMessagePageList) {
            JSONObject json = new JSONObject();
            json.put("id",exp.getId());
            json.put("icon",OSSClientUtil.getObjectUrl(exp.getIcon()));
            json.put("userCode",exp.getUserCode());
            json.put("userMobile",exp.getUserMobile());
            json.put("lastLoginDate",DateTimeUtils.parseStr(exp.getFinalLoginDate()));
            JSONObject jsp = receiveManageService.findDelivererCount(exp.getId(),exp.getSchoolId());/*获取上门人员维护的派单数*/
            json.put("delivererCount",jsp.get("deliverer") == null ? 0 : jsp.get("deliverer"));
            json.put("schoolId",exp.getSchoolId());
            json.put("schoolName",exp.getExpSchool() == null ? "" : exp.getExpSchool().getName());
            json.put("remark",exp.getRemarks());
            list.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", list, pageQuery.page, pageQuery.limit, pageQuery.total), HttpStatus.OK);
    }

    /**
     * @Description:收件订单里的上门人员维护
     * @Author: hanchao
     * @Date: 2018/1/6 0006
     */
    @RequestMapping(value = "/updateTodoorPerson", method = {RequestMethod.POST})
    public ResponseEntity updateTodoorPerson(@RequestBody TodoorPersonForm form) throws Exception {
        if(form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "上门人员id不能为空", null), HttpStatus.OK);
        receiveManageService.updateTodoorPerson(form.id,form.delivererId);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description:收件订单里的条件搜索上门人员列表
     * @Author: hanchao
     * @Date: 2018/1/6 0006
     */
    @RequestMapping(value = "/getAllTodoorPerson", method = {RequestMethod.POST})
    public ResponseEntity getAllTodoorPerson(HttpServletRequest request) throws Exception {
        List<ExpUser> list = receiveManageService.getAllTodoorPerson((List<Integer>) SessionUtils.getSession(request,SessionUtils.SCHOOL_ID));
        JSONArray json = new JSONArray();
        for(ExpUser exp : list){
            JSONObject js = new JSONObject();
            js.put("id",exp.getId());
            js.put("name",exp.getUserCode());
            json.add(js);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功", json), HttpStatus.OK);
    }

    /**
     * @Description:收件订单删除
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    @RequestMapping(value = "/expReceiveOrderDelete", method = {RequestMethod.POST})
    public ResponseEntity expReceiveOrderDelete(@RequestBody IdForm form) throws Exception {
        if(form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不存在", null), HttpStatus.OK);
        receiveManageService.expReceiveOrderDelete(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description:收件订单编辑保存
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    @RequestMapping(value = "/updateExpReceiveOrder", method = {RequestMethod.POST})
    public ResponseEntity updateExpReceiveOrder(@RequestBody ExpReceiveOrderForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        if (!JavaValidate.isMobileNO(form.receiverTel) && StringUtils.isNotBlank(form.receiverTel))
            return new ResponseEntity(new RestResponseEntity(130, "收件人手机号格式不正确", null), HttpStatus.OK);
        receiveManageService.updateExpReceiveOrder(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:收件订单编辑
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    @RequestMapping(value = "/expReceiveOrderEdit", method = {RequestMethod.POST})
    public ResponseEntity expReceiveOrderEdit(@RequestBody IdForm form) throws Exception {
        if(form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        ExpReceiveOrder exp = receiveManageService.get2(ExpReceiveOrder.class,form.id);
        if(exp == null)
            return new ResponseEntity(new RestResponseEntity(120, "收件订单信息不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("id", exp.getId());
        json.put("orderNo", exp.getOrderNo());
        json.put("deliveryNo", exp.getDeliveryNo());/*快递单号*/
        json.put("deliveryId",exp.getDeliveryId());
        json.put("deliveryName",exp.getExpDeliveryCompany() == null ? "" : exp.getExpDeliveryCompany().getName());/*快递公司*/
        json.put("userCode",exp.getExpUser() == null ? "" : exp.getExpUser().getUserCode());
        json.put("userMobile",exp.getExpUser() == null ? "" : exp.getExpUser().getUserMobile());
        json.put("orderPrice", MathUtils.formatObjectReturnDouble(exp.getOrderPrice()));/*订单费用*/
        json.put("payStatus",exp.getPayStatus());/*收费状态*/
        json.put("payStatusName",Commo.parsePayStatus(exp.getPayStatus()));/*收费状态*/
        json.put("payType","");/*支付方式*/
        json.put("payTime","");/*支付时间*/
        Payment pay = receiveManageService.get2(Payment.class,"orderId",exp.getId(),"status",20,"orderType",1);
        if(pay != null){
            json.put("payTime",DateTimeUtils.parseStr(pay.getFinishTime()));
            json.put("payType",pay.getChannel());
        }
        json.put("type",exp.getType());/*取件方式*/
        json.put("typeName",Commo.parseReceiveType(exp.getType()));/*取件方式*/
        json.put("orderStatus",exp.getOrderStatus());/*订单状态*/
        json.put("orderStatusName",Commo.parseReceiveOrderStatus(exp.getOrderStatus()));/*订单状态*/
        json.put("orderTime",DateTimeUtils.parseStr(exp.getCreatedDate()));/*下单时间*/
        json.put("reachDate",DateTimeUtils.parseStr(exp.getArrivedDate()));/*到达时间*/
        json.put("validateCode",exp.getValidateCode());/*验证码*/
        json.put("expressDate",DateTimeUtils.parseStr(exp.getExpressDate()));/*取件时间,签收时间*/
        json.put("schoolId",exp.getSchoolId());
        json.put("schoolName",exp.getExpSchool() == null ? "" : exp.getExpSchool().getName());/*网点名称*/
        json.put("receiverName",exp.getReceiverName());/*收件人名称*/
        json.put("receiverTel",exp.getReceiverTel());/*收件人电话*/
        json.put("province",exp.getProvince());
        if(StringUtils.isNotBlank(exp.getProvince())){
            ExpArea e = receiveManageService.get2(ExpArea.class,"name",exp.getProvince());
            json.put("provinceCode",e.getCode());
            if(StringUtils.isNotBlank(exp.getCity())){
                ExpArea e1 = receiveManageService.get2(ExpArea.class,"name",exp.getCity(),"type",2,"parentCode",e.getCode());
                json.put("cityCode",e1.getCode());
            }
        }
        json.put("city",exp.getCity());
        json.put("zone",exp.getZone());
        json.put("receiverAddress",exp.getReceiverAddress());
        json.put("todoorDate",exp.getBeginEndTime());/*上门时间*/
        if(exp.getDelivererId() != null){
            ExpUser deliverer = receiveManageService.get2(ExpUser.class,"id",exp.getDelivererId(),"logicDeleted",false);
            json.put("delivererId",exp.getDelivererId());/*上门人员*/
            json.put("delivererName",deliverer == null ? "" : deliverer.getUserCode());/*上门人员*/
        }
        json.put("remark",exp.getRemarks());/*备注*/
        json.put("goodsTypeId",exp.getGoodsTypeId());/*物品类型id*/
        json.put("goodsTypeName",exp.getGoodsTypeName());/*物品类型*/
        json.put("goodsDescription",exp.getGoodsDescription());/*物品说明*/
        json.put("capture", OSSClientUtil.getObjectUrl(exp.getCapture()));/*面单签售照片*/
        json.put("captureKey", exp.getCapture());/*面单签售照片*/
        return new ResponseEntity(new RestResponseEntity(100,"成功", json), HttpStatus.OK);
    }

    /**
     * @Description:收件订单的数据统计
     * @Author: hanchao
     * @Date: 2018/1/26 0026
     */
    @RequestMapping(value = "/expReceiveOrderCount", method = {RequestMethod.POST})
    public ResponseEntity expReceiveOrderCount(HttpServletRequest request) throws Exception {
        JSONArray data = new JSONArray();
        Integer todoorQty = 0;/*派件上门数*/
        Integer receiveQty = 0;/*自行取件数*/
        JSONObject page = new JSONObject();
        page.put("schoolIdList",(List<Integer>) SessionUtils.getSession(request,SessionUtils.SCHOOL_ID));
            /*获取收件订单的费用(派件上门)*/
            List<JSONObject> list = receiveManageService.findReceiveOrderCount(page);
            JSONObject json = new JSONObject();
            for(JSONObject jss : list){
                json.put("todoorFeeTotal",MathUtils.formatObjectReturnDouble(jss.get("todoorFeeTotal")));/*上门费用*/
                todoorQty = ((Long)(jss.get("todoorQty") == null ? "0" : jss.get("todoorQty"))).intValue();
                json.put("todoorQty",jss.get("todoorQty"));/*派件上门总件数*/
            }

        /*获取收件订单的费用(自行取件)*/
            List<JSONObject> list1 = receiveManageService.findReceiveQtyCount(page);
            for(JSONObject jss : list1){
//            json.put("todoorQty",jss.get("todoorQty"));/*自行取件总件数*/
                receiveQty = ((Long)(jss.get("todoorQty") == null ? "0" : jss.get("todoorQty"))).intValue();
                json.put("receiveQty",receiveQty);
            }
            Integer totalQty = todoorQty + receiveQty;/*订单数*/
            json.put("totalQty",totalQty);
            data.add(json);
        return new ResponseEntity(new RestResponseEntity(100,"成功", data), HttpStatus.OK);
    }

   /**
    * @Description:收件订单列表批量导出
    * @Author: hanchao
    * @Date: 2018/1/27 0027
    */
    @RequestMapping(value = "/exportExcelPageList", method = {RequestMethod.GET})
    public void getExcel(HttpServletRequest request,HttpServletResponse response,
                         @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                         @RequestParam(name = "userCode", required = false) String userCode,
                         @RequestParam(name = "userMobile", required = false) String userMobile,
                         @RequestParam(name = "type", required = false) Integer type,
                         @RequestParam(name = "orderStatus", required = false) Integer orderStatus,
                         @RequestParam(name = "delivererId", required = false) Integer delivererId,
                         @RequestParam(name = "orderStartTime", required = false) String orderStartTime,
                         @RequestParam(name = "orderEndTime", required = false) String orderEndTime,
                         @RequestParam(name = "expressStartTime", required = false) String expressStartTime,
                         @RequestParam(name = "expressEndTime", required = false) String expressEndTime,
                         @RequestParam(name = "isDelete", required = false) Boolean isDelete,
                         @RequestParam(name = "orderNo", required = false) String orderNo,
                         @RequestParam(name = "deliveryNo", required = false) String deliveryNo,
                         @RequestParam(name = "schoolId", required = false) Integer schoolId)throws Exception {
        PageQuery pageQuery = new PageQuery(1);
        pageQuery.limit = 9999;
        List<ExpReceiveOrder> receiveOrderList = receiveManageService.findReceiveOrderPageList(pageQuery,isDelete,userCode,userMobile,
                type,orderStatus,delivererId,orderStartTime,orderEndTime,expressStartTime,expressEndTime,schoolId,(List<Integer>) SessionUtils.getSession(request, SessionUtils.SCHOOL_ID),
                orderNo, deliveryNo);
        //导出文件的标题
        String title = "收件订单列表"+DateTimeUtils.parseStr(new Date(),"yyyy-MM-dd")+".xls";
        //设置表格标题行
        String[] headers = new String[] {"编号","订单号","快递单号", "快递公司","用户名","手机号", "费用","上门小费", "收费状态", "支付方式", "支付时间", "派件方式", "订单状态", "下单时间", "到达时间", "验证码", "签收时间", "所属网点", "收件人名称", "收件人电话", "收件人地址", "上门时间", "上门人员", "备注", "物品类型", "物品说明"};
        List<Object[]> dataList = new ArrayList<Object[]>();
        Object[] objs = null;
        for (ExpReceiveOrder exp : receiveOrderList) {//循环每一条数据
            objs = new Object[headers.length];
            objs[1] = exp.getOrderNo() == null ? "" : exp.getOrderNo();
            objs[2] = exp.getDeliveryNo() == null ? "" : exp.getDeliveryNo();
            objs[3] = exp.getExpDeliveryCompany() == null ? "" : exp.getExpDeliveryCompany().getName();
            objs[4] = exp.getExpUser() == null ? "" : exp.getExpUser().getUserCode();
            objs[5] = exp.getExpUser() == null ? "" : exp.getExpUser().getUserMobile();
            objs[6] = MathUtils.formatObjectReturnDouble(exp.getOrderPrice()) == null ? "" : MathUtils.formatObjectReturnDouble(exp.getOrderPrice()) ;
            objs[7] = MathUtils.formatObjectReturnDouble(exp.getTodoorTipFee()) == null ? "" : MathUtils.formatObjectReturnDouble(exp.getTodoorTipFee());
            objs[8] = Commo.parsePayStatus(exp.getPayStatus()) == null ? "" : Commo.parsePayStatus(exp.getPayStatus());
            Payment pay = receiveManageService.get2(Payment.class,"orderId",exp.getId(),"status",20,"orderType",1);
            if(pay != null){
                objs[9] = pay.getChannel() == null ? "" : pay.getChannel();//支付方式
                objs[10] = DateTimeUtils.parseStr(pay.getFinishTime()) == null ? "" : DateTimeUtils.parseStr(pay.getFinishTime());
            }
            objs[11] = Commo.parseReceiveType(exp.getType()) == null ? "" : Commo.parseReceiveType(exp.getType());
            objs[12] = Commo.parseReceiveOrderStatus(exp.getOrderStatus()) == null ? "" : Commo.parseReceiveOrderStatus(exp.getOrderStatus());
            objs[13] = DateTimeUtils.parseStr(exp.getCreatedDate()) == null ? "" : DateTimeUtils.parseStr(exp.getCreatedDate());
            objs[14] = DateTimeUtils.parseStr(exp.getArrivedDate()) == null ? "" : DateTimeUtils.parseStr(exp.getArrivedDate());
            objs[15] = exp.getValidateCode() == null ? "" : exp.getValidateCode();
            objs[16] = DateTimeUtils.parseStr(exp.getExpressDate()) == null ? "" : DateTimeUtils.parseStr(exp.getExpressDate());
            objs[17] = exp.getExpSchool() == null ? "" : exp.getExpSchool().getName();
            objs[18] = exp.getReceiverName() == null ? "" : exp.getReceiverName();
            objs[19] = exp.getReceiverTel() == null ? "" : exp.getReceiverTel();
            StringBuilder sb = new StringBuilder();
            sb.append(exp.getProvince() == null ? "" : exp.getProvince());
            sb.append(exp.getCity() == null ? "" : exp.getCity());
            sb.append(exp.getZone() == null ? "" : exp.getZone());
            sb.append(exp.getReceiverAddress() == null ? "" : exp.getReceiverAddress());
            objs[20] = sb == null ? "" : sb;
            objs[21] = exp.getBeginEndTime() == null ? "" : exp.getBeginEndTime();
            if(exp.getDelivererId() != null){
                ExpUser deliverer = receiveManageService.get2(ExpUser.class,"id",exp.getDelivererId(),"logicDeleted",false);
                objs[22] = deliverer == null ? "" : deliverer.getUserCode();
            }
            objs[23] = exp.getRemarks() == null ? "" : exp.getRemarks();
            objs[24] = exp.getGoodsTypeName() == null ? "" : exp.getGoodsTypeName();
            objs[25] = exp.getGoodsDescription() == null ? "" : exp.getGoodsDescription();
            //数据添加到excel表格
            dataList.add(objs);
        }
        ExportExcelUtil.exportExcel(request, response, title, headers, dataList,null);
    }

    /**
     * @Description:收件订单列表
     * @Author: hanchao
     * @Date: 2018/1/6 0006
     */
    @RequestMapping(value = "/expReceiveOrderPageList", method = {RequestMethod.GET})
    public ResponseEntity expReceiveOrderPageList(HttpServletRequest request,
                                                  @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  @RequestParam(name = "userCode", required = false) String userCode,
                                                  @RequestParam(name = "userMobile", required = false) String userMobile,
                                                  @RequestParam(name = "type", required = false) Integer type,
                                                  @RequestParam(name = "orderStatus", required = false) Integer orderStatus,
                                                  @RequestParam(name = "delivererId", required = false) Integer delivererId,
                                                  @RequestParam(name = "orderStartTime", required = false) String orderStartTime,
                                                  @RequestParam(name = "orderEndTime", required = false) String orderEndTime,
                                                  @RequestParam(name = "expressStartTime", required = false) String expressStartTime,
                                                  @RequestParam(name = "expressEndTime", required = false) String expressEndTime,
                                                  @RequestParam(name = "isDelete", required = false) Boolean isDelete,
                                                  @RequestParam(name = "orderNo", required = false) String orderNo,
                                                  @RequestParam(name = "deliveryNo", required = false) String deliveryNo,
                                                  @RequestParam(name = "schoolId", required = false) Integer schoolId)throws Exception {
        if (pageSize >= 100)
            return new ResponseEntity(new RestResponseEntity(110, "条数过长", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(pageNum);
        pageQuery.limit = pageSize;
        List<ExpReceiveOrder> receiveOrderList = receiveManageService.findReceiveOrderPageList(pageQuery,isDelete,userCode,userMobile,
                type,orderStatus,delivererId,orderStartTime,orderEndTime,expressStartTime,expressEndTime,schoolId,(List<Integer>) SessionUtils.getSession(request, SessionUtils.SCHOOL_ID),
                orderNo, deliveryNo);
        JSONArray data = new JSONArray();
        for (ExpReceiveOrder exp : receiveOrderList) {
            JSONObject json = new JSONObject();
            json.put("id", exp.getId());
            json.put("orderNo", exp.getOrderNo());
            json.put("deliveryNo", exp.getDeliveryNo());/*快递单号*/
            json.put("deliveryId",exp.getDeliveryId());
            json.put("deliveryName",exp.getExpDeliveryCompany() == null ? "" : exp.getExpDeliveryCompany().getName());/*快递公司*/
            json.put("deliveryCom",exp.getExpDeliveryCompany() == null ? "" : exp.getExpDeliveryCompany().getCom());/*快递com*/
            json.put("userCode",exp.getExpUser() == null ? "" : exp.getExpUser().getUserCode());
            json.put("userMobile",exp.getExpUser() == null ? "" : exp.getExpUser().getUserMobile());
            json.put("orderPrice",MathUtils.formatObjectReturnDouble(exp.getOrderPrice()));/*订单费用*/
            json.put("todoorFee",MathUtils.formatObjectReturnDouble(exp.getTodoorFee()));/*上门费*/
            json.put("todoorTipFee",MathUtils.formatObjectReturnDouble(exp.getTodoorTipFee()));/*上门小费*/
            json.put("payStatus",exp.getPayStatus());/*收费状态*/
            json.put("payStatusName",Commo.parsePayStatus(exp.getPayStatus()));/*收费状态*/
            json.put("payType","");/*支付方式*/
            json.put("payTime","");/*支付时间*/
            Payment pay = receiveManageService.get2(Payment.class,"orderId",exp.getId(),"status",20,"orderType",1);
            if(pay != null){
                json.put("payTime",DateTimeUtils.parseStr(pay.getFinishTime()));
                json.put("payType",pay.getChannel());
            }
            json.put("type",exp.getType());/*取件方式*/
            json.put("typeName",Commo.parseReceiveType(exp.getType()));/*取件方式*/
            json.put("orderStatus",exp.getOrderStatus());/*订单状态*/
            json.put("orderStatusName",Commo.parseReceiveOrderStatus(exp.getOrderStatus()));/*订单状态*/
            json.put("orderTime",DateTimeUtils.parseStr(exp.getCreatedDate()));/*下单时间*/
            json.put("reachDate",DateTimeUtils.parseStr(exp.getArrivedDate()));/*到达时间*/
            json.put("validateCode",exp.getValidateCode());/*验证码*/
            json.put("expressDate",DateTimeUtils.parseStr(exp.getExpressDate()));/*取件时间,签收时间*/
            json.put("schoolId",exp.getSchoolId());
            json.put("schoolName",exp.getExpSchool() == null ? "" : exp.getExpSchool().getName());/*网点名称*/
            json.put("receiverName",exp.getReceiverName());/*收件人名称*/
            json.put("receiverTel",exp.getReceiverTel());/*收件人电话*/
            StringBuilder sb = new StringBuilder();
            sb.append(exp.getProvince() == null ? "" : exp.getProvince());
            sb.append(exp.getCity() == null ? "" : exp.getCity());
            sb.append(exp.getZone() == null ? "" : exp.getZone());
            sb.append(exp.getReceiverAddress() == null ? "" : exp.getReceiverAddress());
            json.put("receiverAddress",sb);/*收件人地址*/
            json.put("todoorDate",exp.getBeginEndTime());/*上门时间*/
            if(exp.getDelivererId() != null){
                ExpUser deliverer = receiveManageService.get2(ExpUser.class,"id",exp.getDelivererId(),"logicDeleted",false);
                json.put("delivererId",exp.getDelivererId());/*上门人员*/
                json.put("delivererName",deliverer == null ? "" : deliverer.getUserCode());/*上门人员*/
            }
            json.put("remark",exp.getRemarks());/*备注*/
            json.put("goodsTypeId",exp.getGoodsTypeId());/*物品类型id*/
            json.put("goodsTypeName",exp.getGoodsTypeName());/*物品类型*/
            json.put("goodsDescription",exp.getGoodsDescription());/*物品说明*/
            json.put("capture",OSSClientUtil.getObjectUrl(exp.getCapture()));/*面单照片*/
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.page, pageQuery.limit, pageQuery.total), HttpStatus.OK);
    }

    /**
     * @Description:到件信息列表导出
     * @Author: hanchao
     * @Date: 2018/1/27 0027
     */
    @RequestMapping(value = "/arriveMessageExportExcelPageList", method = {RequestMethod.GET})
    public void arriveMessageExportExcelPageList(HttpServletRequest request,HttpServletResponse response,
                                                @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                @RequestParam(name = "receiverTel", required = false) String receiverTel,
                                                @RequestParam(name = "deliveryNo", required = false) String deliveryNo,
                                                @RequestParam(name = "schoolId", required = false) Integer schoolId)throws Exception {
        PageQuery pageQuery = new PageQuery(1);
        pageQuery.limit = 9999;
        pageQuery.limit = pageSize;
        List<ExpReceiveOrder> arriveMessagePageList = receiveManageService.findArriveMessagePageList(pageQuery,receiverTel,schoolId,(List<Integer>) SessionUtils.getSession(request,SessionUtils.SCHOOL_ID),deliveryNo);
        //导出文件的标题
        String title = "到件订单列表"+DateTimeUtils.parseStr(new Date(),"yyyy-MM-dd")+".xls";
        //设置表格标题行
        String[] headers = new String[] {"编号","快递单号","快递公司", "到达时间","验证码","签收时间", "所属网点", "收件人电话", "本地是否存在用户", "备注"};
        List<Object[]> dataList = new ArrayList<Object[]>();
        Object[] objs = null;
        for (ExpReceiveOrder exp : arriveMessagePageList) {//循环每一条数据
            objs = new Object[headers.length];
            objs[1] = exp.getDeliveryNo() == null ? "" : exp.getDeliveryNo();
            objs[2] = exp.getExpDeliveryCompany() == null ? "" : exp.getExpDeliveryCompany().getName();
            objs[3] = DateTimeUtils.parseStr(exp.getArrivedDate()) == null ? "" : DateTimeUtils.parseStr(exp.getArrivedDate());
            objs[4] = exp.getValidateCode() == null ? "" : exp.getValidateCode();
            objs[5] = DateTimeUtils.parseStr(exp.getExpressDate()) == null ? "" : DateTimeUtils.parseStr(exp.getExpressDate());
            objs[6] = exp.getExpSchool() == null ? "" : exp.getExpSchool().getName();
            objs[7] = exp.getReceiverTel() == null ? "" : exp.getReceiverTel();
            objs[8] = "否";/*是否存在本地用户*/
            if(StringUtils.isNotBlank(exp.getReceiverTel()) && exp.getSchoolId() != null){
                List<ExpReceiveOrder> expReceiveOrder = receiveManageService.selectIsThisUser(exp.getId(),exp.getReceiverTel(),exp.getSchoolId());
                objs[8] = expReceiveOrder.size() == 0 ? "否" : "是";/*是否存在本地用户*/
            }
            objs[9] = exp.getRemarks() == null ? "" : exp.getRemarks();
            //数据添加到excel表格
            dataList.add(objs);
        }
        ExportExcelUtil.exportExcel(request, response, title, headers, dataList,null);
    }

    /**
     * @Description:到件信息列表
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    @RequestMapping(value = "/arriveMessagePageList", method = {RequestMethod.GET})
    public ResponseEntity arriveMessagePageList(HttpServletRequest request,
                                             @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                             @RequestParam(name = "receiverTel", required = false) String receiverTel,
                                             @RequestParam(name = "deliveryNo", required = false) String deliveryNo,
                                             @RequestParam(name = "schoolId", required = false) Integer schoolId)throws Exception {
        if (pageSize >= 100)
            return new ResponseEntity(new RestResponseEntity(110, "条数过长", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(pageNum);
        pageQuery.limit = pageSize;
        List<ExpReceiveOrder> arriveMessagePageList = receiveManageService.findArriveMessagePageList(pageQuery,receiverTel,schoolId,(List<Integer>) SessionUtils.getSession(request,SessionUtils.SCHOOL_ID),
                deliveryNo);
        JSONArray list = new JSONArray();
        for (ExpReceiveOrder exp : arriveMessagePageList) {
            JSONObject json = new JSONObject();
            json.put("id", exp.getId());
            json.put("deliveryNo",exp.getDeliveryNo());
            json.put("deliveryId",exp.getDeliveryId());
            json.put("deliveryName",exp.getExpDeliveryCompany() == null ? "" : exp.getExpDeliveryCompany().getName());
            json.put("reachDate",DateTimeUtils.parseStr(exp.getArrivedDate()));/*到达时间*/
            json.put("validateCode",exp.getValidateCode());/*验证码*/
            json.put("expressDate",DateTimeUtils.parseStr(exp.getExpressDate()));/*签收时间*/
            json.put("schoolId",exp.getSchoolId());
            json.put("schoolName",exp.getExpSchool() == null ? "" : exp.getExpSchool().getName());
            json.put("receiverTel",exp.getReceiverTel());
            json.put("isThisUser","否");/*是否存在本地用户*/
            if(StringUtils.isNotBlank(exp.getReceiverTel()) && exp.getSchoolId() != null){
                List<ExpReceiveOrder> expReceiveOrder = receiveManageService.selectIsThisUser(exp.getId(),exp.getReceiverTel(),exp.getSchoolId());
                json.put("isThisUser",expReceiveOrder.size() == 0 ? "否" : "是");/*是否存在本地用户*/
            }
            json.put("remark",exp.getRemarks());
            list.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", list, pageQuery.page, pageQuery.limit, pageQuery.total), HttpStatus.OK);
    }

}
