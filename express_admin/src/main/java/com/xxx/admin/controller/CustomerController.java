package com.xxx.admin.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.ExpCustomerForm;
import com.xxx.admin.form.ExpSchoolForm;
import com.xxx.admin.form.ExpressAddressForm;
import com.xxx.admin.form.IdForm;
import com.xxx.admin.service.CustomerService;
import com.xxx.admin.service.SchoolService;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.ExpReceiveAddress;
import com.xxx.model.business.ExpSchool;
import com.xxx.model.business.ExpSendAddress;
import com.xxx.model.business.ExpUser;
import com.xxx.user.Commo;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.utils.SessionUtils;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.ExportExcelUtil;
import com.xxx.utils.JavaValidate;
import com.xxx.utils.OSSClientUtil;
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
import java.util.concurrent.Semaphore;


@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * @Description:客户列表信息批量导出
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    @RequestMapping(value = "/exportExcel", method = {RequestMethod.GET})
    public void getExcel(HttpServletRequest request,HttpServletResponse response,
                         @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                         @RequestParam(name = "name", required = false) String name,
                         @RequestParam(name = "userMobile", required = false) String userMobile,
                         @RequestParam(name = "isDelete", required = false) Boolean isDelete,
                         @RequestParam(name = "schoolId", required = false) Integer schoolId)throws Exception {
        PageQuery pageQuery = new PageQuery(1);
        pageQuery.limit = 9999;
        List<ExpUser> customerList = customerService.findCustomerPageList(pageQuery, name,userMobile,isDelete,schoolId,CurrentUser.get().list);
        //导出文件的标题
        String title = "客户列表"+DateTimeUtils.parseStr(new Date(),"yyyy-MM-dd")+".xls";
        //设置表格标题行
        String[] headers = new String[] {"用户名","手机号", "最后登录时间","第三方渠道","第三方渠道编号", "所属网点", "备注"};
        List<Object[]> dataList = new ArrayList<Object[]>();
        Object[] objs = null;
        for (ExpUser exp : customerList) {//循环每一条数据
            objs = new Object[headers.length];
            objs[1] = exp.getUserCode() == null ? "" : exp.getUserCode();
            objs[2] = exp.getUserMobile() == null ? "" : exp.getUserMobile();
            objs[3] = exp.getFinalLoginDate() == null ? "" : DateTimeUtils.parseStr(exp.getFinalLoginDate());
//            objs[4] = exp.get == null ? "" : ;
//            objs[5] = exp.get == null ? "" : ;
            objs[6] = exp.getExpSchool() == null ? "" : exp.getExpSchool().getName();
            objs[7] = exp.getRemarks() == null ? "" : exp.getRemarks();
            //数据添加到excel表格
            dataList.add(objs);
        }
        ExportExcelUtil.exportExcel(request, response, title, headers, dataList,null);
    }

    /**
     * @Description: 获取客户列表
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    @RequestMapping(value = "/customerPageList", method = {RequestMethod.GET})
    public ResponseEntity customerPageList(HttpServletRequest request,
                               @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                               @RequestParam(name = "name", required = false) String name,
                               @RequestParam(name = "userMobile", required = false) String userMobile,
                               @RequestParam(name = "isDelete", required = false) Boolean isDelete,
                               @RequestParam(name = "schoolId", required = false) Integer schoolId)throws Exception {
        if (pageSize >= 100)
            return new ResponseEntity(new RestResponseEntity(110, "条数过长", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(pageNum);
        pageQuery.limit = pageSize;
        if(StringUtils.isNotBlank(name)){
            name = new String(name.getBytes("iso8859-1"),"utf-8");
        }
        if(StringUtils.isNotBlank(userMobile)){
            userMobile = new String(userMobile.getBytes("iso8859-1"),"utf-8");
        }

        List<ExpUser> customerList = customerService.findCustomerPageList(pageQuery,name,userMobile,isDelete,schoolId, (List<Integer>) SessionUtils.getSession(request, SessionUtils.SCHOOL_ID));
        JSONArray list = new JSONArray();
        for (ExpUser exp : customerList) {
            JSONObject json = new JSONObject();
            json.put("id", exp.getId());
            json.put("icon", OSSClientUtil.getObjectUrl(exp.getIcon()));
            json.put("userCode",exp.getUserCode());
            json.put("userNickname",exp.getUserNickname());
            json.put("userMobile",exp.getUserMobile());
            json.put("finalLoginDate",DateTimeUtils.parseStr(exp.getFinalLoginDate()));
            json.put("schoolId",exp.getExpSchool() == null ? "" : exp.getExpSchool().getId());
            json.put("schoolName",exp.getExpSchool() == null ? "" : exp.getExpSchool().getName());
            json.put("remark",exp.getRemarks());
            list.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", list, pageQuery.page, pageQuery.limit, pageQuery.total), HttpStatus.OK);
    }

    /**
     * @Description:客户编辑
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    @RequestMapping(value = "/customerEdit", method = {RequestMethod.POST})
    public ResponseEntity customerEdit(@RequestBody IdForm form) throws Exception {
       if(form.id == null)
           return new ResponseEntity(new RestResponseEntity(110, "客户id不存在", null), HttpStatus.OK);
        ExpUser expUser = customerService.get2(ExpUser.class,"id",form.id,"logicDeleted",false);
        if(expUser == null)
            return new ResponseEntity(new RestResponseEntity(120, "客户不存在", null), HttpStatus.OK);
            JSONObject json = new JSONObject();
            json.put("id", expUser.getId());
            json.put("icon", OSSClientUtil.getObjectUrl(expUser.getIcon()));
//            json.put("iconKey",expUser.getIcon());
            json.put("userCode", expUser.getUserCode());
            json.put("userMobile",expUser.getUserMobile());
            json.put("finalLoginDate",DateTimeUtils.parseStr(expUser.getFinalLoginDate()));
            json.put("userNickname",expUser.getUserNickname());
            json.put("schoolId",expUser.getExpSchool() == null ? "" : expUser.getExpSchool().getId());
            json.put("schoolName",expUser.getExpSchool() == null ? "" : expUser.getExpSchool().getName());
            json.put("remark",expUser.getRemarks());
        return new ResponseEntity(new RestResponseEntity(100,"成功", json), HttpStatus.OK);
    }

    /**
     * @Description:客户编辑保存
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    @RequestMapping(value = "/updateCustomer", method = {RequestMethod.POST})
    public ResponseEntity updateExpSchool(@RequestBody ExpCustomerForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "客户id不能为空", null), HttpStatus.OK);
        if( StringUtils.isBlank(form.userCode) || !form.userCode.matches("^[0-9a-zA-Z_]{6,12}$"))
            return new ResponseEntity(new RestResponseEntity(120, "用户名必须为6-12位字母、数字、下划线组成", null), HttpStatus.OK);
        if (!JavaValidate.isMobileNO(form.userMobile) || StringUtils.isBlank(form.userMobile))
            return new ResponseEntity(new RestResponseEntity(130, "手机号格式不正确", null), HttpStatus.OK);
        if(StringUtils.isBlank(form.userNickname)){
            return new ResponseEntity(new RestResponseEntity(130, "用户昵称不能为空", null), HttpStatus.OK);
        }
        ExpUser exp = customerService.selectIsRepetitionUserCode(form.id,form.userCode);
        if(exp != null){
            return new ResponseEntity(new RestResponseEntity(140, "用户名已存在", null), HttpStatus.OK);
        }
         exp = customerService.selectIsRepetitionUserMobile(form.id,form.userMobile);
        if(exp != null){
            return new ResponseEntity(new RestResponseEntity(150, "手机号已存在", null), HttpStatus.OK);
        }
        customerService.updateCustomer(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:客户信息逻辑删除
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    @RequestMapping(value = "/customerDelete", method = {RequestMethod.POST})
    public ResponseEntity customerDelete(@RequestBody IdForm form) throws Exception {
        if(form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "客户id不存在", null), HttpStatus.OK);
        customerService.customerDelete(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description:客户寄件地址
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    @RequestMapping(value = "/expSendAddressPageList", method = {RequestMethod.GET})
    public ResponseEntity expSendAddress( HttpServletRequest request,
                                          @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                          @RequestParam(name = "id", required = false) Integer id) throws Exception {
        if (pageSize >= 100)
            return new ResponseEntity(new RestResponseEntity(110, "条数过长", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(pageNum);
        pageQuery.limit = pageSize;
        if(id == null)
            return new ResponseEntity(new RestResponseEntity(110, "客户id不存在", null), HttpStatus.OK);
        List<ExpSendAddress> expUserList = customerService.getExpSendAddressList(pageQuery,id);
        if(expUserList.size() == 0)
            return new ResponseEntity(new RestResponseEntity(120, "当前用户暂无地址信息", null), HttpStatus.OK);
        JSONArray json = new JSONArray();
        for(ExpSendAddress exp : expUserList){
            JSONObject js = new JSONObject();
            js.put("id",exp.getUserId());
            js.put("addressId",exp.getId());
            js.put("name",exp.getName());
            js.put("tel",exp.getTel());
//            js.put("province",exp.getProvince());
//            js.put("city",exp.getCity());
//            js.put("zone",exp.getZone());
//            js.put("address",exp.getProvince()+exp.getCity()+exp.getZone()+exp.getAddress());
            StringBuilder sb = new StringBuilder();
            sb.append(exp.getProvince() == null ? "" : exp.getProvince());
            sb.append(exp.getCity() == null ? "" : exp.getCity());
            sb.append(exp.getZone() == null ? "" : exp.getZone());
            sb.append(exp.getAddress() == null ? "" : exp.getAddress());
            js.put("address",sb);
            js.put("isDefault","否");
            if(exp.getDefault() != null){
                js.put("isDefault",exp.getDefault() == true ? "是" : "否");
            }
            json.add(js);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", json, pageQuery.page, pageQuery.limit, pageQuery.total), HttpStatus.OK);
    }

    /**
     * @Description:修改客户寄件地址为默认
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    @RequestMapping(value = "/expSendAddressDefault", method = {RequestMethod.POST})
    public ResponseEntity expSendAddressDefault(@RequestBody ExpressAddressForm form) throws Exception {
        if(form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "地址id不能为空", null), HttpStatus.OK);
        customerService.expSendAddressDefault(form.id,form.addressId);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }
    /**
     * @Description:客户收件地址
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    @RequestMapping(value = "/ExpReceiveAddressPageList", method = {RequestMethod.GET})
    public ResponseEntity ExpReceiveAddress(HttpServletRequest request,
                                            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                            @RequestParam(name = "id", required = false) Integer id) throws Exception {
        if (pageSize >= 100)
            return new ResponseEntity(new RestResponseEntity(110, "条数过长", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(pageNum);
        pageQuery.limit = pageSize;
        if(id == null)
            return new ResponseEntity(new RestResponseEntity(110, "客户id不存在", null), HttpStatus.OK);
        List<ExpReceiveAddress> expUserList = customerService.getExpReceiveAddressList(pageQuery,id);
        if(expUserList.size() == 0)
            return new ResponseEntity(new RestResponseEntity(120, "当前用户暂无地址信息", null), HttpStatus.OK);
        JSONArray json = new JSONArray();
        for(ExpReceiveAddress exp : expUserList){
            JSONObject js = new JSONObject();
            js.put("id",exp.getUserId());
            js.put("addressId",exp.getId());
            js.put("name",exp.getName());
            js.put("tel",exp.getTel());
            StringBuilder sb = new StringBuilder();
            sb.append(exp.getProvince() == null ? "" : exp.getProvince());
            sb.append(exp.getCity() == null ? "" : exp.getCity());
            sb.append(exp.getZone() == null ? "" : exp.getZone());
            sb.append(exp.getAddress() == null ? "" : exp.getAddress());
//            js.put("province",exp.getProvince());
//            js.put("city",exp.getCity());
//            js.put("zone",exp.getZone());
//            js.put("address",exp.getProvince()+exp.getCity()+exp.getZone()+exp.getAddress());
            js.put("address",sb);
            js.put("isDefault","否");
            if(exp.getDefault() != null){
                js.put("isDefault",exp.getDefault() == true ? "是" : "否");
            }
            json.add(js);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", json, pageQuery.page, pageQuery.limit, pageQuery.total), HttpStatus.OK);
    }

    /**
     * @Description:修改客户收件地址为默认
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    @RequestMapping(value = "/ExpReceiveAddressDefault", method = {RequestMethod.POST})
    public ResponseEntity ExpReceiveAddressDefault(@RequestBody ExpressAddressForm form) throws Exception {
        if(form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "地址id不能为空", null), HttpStatus.OK);
        if(form.addressId == null)
            return new ResponseEntity(new RestResponseEntity(120, "地址信息有误", null), HttpStatus.OK);
        customerService.ExpReceiveAddressDefault(form.id,form.addressId);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

}
