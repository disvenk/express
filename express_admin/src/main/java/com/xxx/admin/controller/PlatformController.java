package com.xxx.admin.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.ExpCustomerForm;
import com.xxx.admin.form.ExpressAddressForm;
import com.xxx.admin.form.IdForm;
import com.xxx.admin.service.CustomerService;
import com.xxx.admin.service.PlatformService;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.ExpReceiveAddress;
import com.xxx.model.business.ExpSendAddress;
import com.xxx.model.business.ExpUser;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.ExportExcelUtil;
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
@RequestMapping("/platform")
public class PlatformController {

    @Autowired
    private PlatformService platformService;

    /**
     * @Description:平台用户列表信息批量导出
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    @RequestMapping(value = "/exportExcel", method = {RequestMethod.GET})
    public void getExcel(HttpServletRequest request,HttpServletResponse response,
                         @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                         @RequestParam(name = "name", required = false) String name,
                         @RequestParam(name = "userMobile", required = false) String userMobile,
                         @RequestParam(name = "schoolId", required = false) Integer schoolId)throws Exception {
        PageQuery pageQuery = new PageQuery(1);
        pageQuery.limit = 9999;

        List<ExpUser> customerList = platformService.findCustomerPageList(pageQuery, name,userMobile,schoolId);
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
     * @Description: 获取平台用户列表
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    @RequestMapping(value = "/platformPageList", method = {RequestMethod.GET})
    public ResponseEntity customerPageList(HttpServletRequest request,
                               @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                               @RequestParam(name = "name", required = false) String name,
                               @RequestParam(name = "userMobile", required = false) String userMobile,
                               @RequestParam(name = "schoolId", required = false) Integer schoolId)throws Exception {
        if (pageSize >= 100)
            return new ResponseEntity(new RestResponseEntity(110, "条数过长", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(pageNum);
        pageQuery.limit = pageSize;

        List<ExpUser> customerList = platformService.findCustomerPageList(pageQuery, name,userMobile,schoolId);
        JSONArray list = new JSONArray();
        for (ExpUser exp : customerList) {
            JSONObject json = new JSONObject();
            json.put("id", exp.getId());
//            json.put("icon", OSSClientUtil.getObjectUrl(exp.getIcon()));
            json.put("userCode",exp.getUserCode());
            json.put("userMobile",exp.getUserMobile());
            json.put("finalLoginDate",DateTimeUtils.parseStr(exp.getFinalLoginDate()));
            json.put("schoolId",exp.getExpSchool() == null ? "" : exp.getExpSchool().getId());
            json.put("schoolName",exp.getExpSchool() == null ? "" : exp.getExpSchool().getName());
            json.put("remark",exp.getRemarks());
            list.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", list, pageQuery.page, pageQuery.limit, pageQuery.total), HttpStatus.OK);
    }


}
