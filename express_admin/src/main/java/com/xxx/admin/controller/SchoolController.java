package com.xxx.admin.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.ExpSchoolForm;
import com.xxx.admin.form.IdForm;
import com.xxx.admin.form.ReceiveTime;
import com.xxx.admin.form.SendTime;
import com.xxx.admin.service.RoleService;
import com.xxx.admin.service.SchoolService;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.ExpSchool;
import com.xxx.model.business.ExpSchoolWorkTime;
import com.xxx.user.Commo;
import com.xxx.user.utils.SessionUtils;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.date.DateTimeUtil;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


@Controller
@RequestMapping("school")
public class SchoolController {

    @Autowired
    private SchoolService schoolService;
    @Autowired
    private RoleService roleService;

    /**
     * @Description:根据用户获取授权网点
     * @Author: disvenk.dai
     * @Date: 2018/1/11
     */
    @RequestMapping(value = "/getAllSchoolList", method = {RequestMethod.POST})
    public ResponseEntity getSchoolListByUser() throws Exception {
        List<ExpSchool> expSchoolList = roleService.getAllSchoolListByUser();
        JSONArray data = new JSONArray();
        for (ExpSchool exp : expSchoolList) {
            JSONObject json = new JSONObject();
            json.put("id", exp.getId());
            json.put("name", exp.getName());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功", data), HttpStatus.OK);
    }
    /**
     * @Description:获取所有网点信息
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     *//*
    @RequestMapping(value = "/getAllSchoolListAdmin", method = {RequestMethod.POST})
    public ResponseEntity getAllSchoolList() throws Exception {
        List<ExpSchool> expSchoolList = schoolService.getAllSchoolList();
        JSONArray data = new JSONArray();
        for (ExpSchool exp : expSchoolList) {
            JSONObject json = new JSONObject();
            json.put("id", exp.getId());
            json.put("name", exp.getName());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功", data), HttpStatus.OK);
    }*/

    /**
     * @Description: 获取网点列表
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    @RequestMapping(value = "/schoolPageList", method = {RequestMethod.GET})
    public ResponseEntity schoolPageList(HttpServletRequest request,
                               @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                               @RequestParam(name = "name", required = false) String name) throws Exception {
        if (pageSize >= 100)
            return new ResponseEntity(new RestResponseEntity(110, "条数过长", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(pageNum);
        pageQuery.limit = pageSize;
        if(StringUtils.isNotBlank(name)){
            name = new String(name.getBytes("iso8859-1"),"utf-8");
        }
        List<ExpSchool> schoolList = schoolService.findExpSchoolList(pageQuery, name, (List<Integer>) SessionUtils.getSession(request,SessionUtils.SCHOOL_ID));
        JSONArray list = new JSONArray();
        for (ExpSchool school : schoolList) {
            JSONObject json = new JSONObject();
            json.put("id", school.getId());
            json.put("name", school.getName());
            json.put("capture", school.getCapture() == null || school.getCapture() == false ? "否" : "是");
            json.put("takedAddress",school.getTakedAddress());
            list.add(json);
        }

        return new ResponseEntity(new PageResponseEntity(100, "成功", list, pageQuery.page, pageQuery.limit, pageQuery.total), HttpStatus.OK);
    }

    /**
     * @Description:网点编辑
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    @RequestMapping(value = "/schoolEdit", method = {RequestMethod.POST})
    public ResponseEntity schoolEdit(@RequestBody IdForm form) throws Exception {
       if(form.id == null)
           return new ResponseEntity(new RestResponseEntity(110, "网点id不存在", null), HttpStatus.OK);
        ExpSchool expSchool = schoolService.get2(ExpSchool.class,form.id);
        if(expSchool == null)
            return new ResponseEntity(new RestResponseEntity(120, "网点不存在", null), HttpStatus.OK);
            JSONObject json = new JSONObject();
            json.put("id", expSchool.getId());
            json.put("name", expSchool.getName());
            /*面单是否拍照*/
            json.put("captureName", expSchool.getCapture() == null || expSchool.getCapture() == false ? "否" : "是");
            json.put("ispaper",expSchool.getIspaper());
            json.put("ispaperName", Commo.getIspaper(expSchool.getIspaper()));
            json.put("takedAddress",expSchool.getTakedAddress());
            List<ExpSchoolWorkTime> expList = schoolService.selectTimeById(expSchool.getId());//获取维护时间信息
            JSONArray jsonArray = new JSONArray();
            JSONArray jsonArray1 = new JSONArray();
            for(ExpSchoolWorkTime exp : expList){
                JSONObject js = new JSONObject();
                //0 上门取件   1 派件上门
                if(exp.getType() == 1){
                    js.put("type",exp.getType());
                    js.put("beginTime",exp.getBeginDate() == null ? "" : exp.getBeginDate().toString().substring(11,13));
                    js.put("endTime",exp.getEndDate() == null ? "" : exp.getEndDate().toString().substring(11,13));
                    jsonArray.add(js);
                }
                if(exp.getType() == 0){
                    js.put("type",exp.getType());
                    js.put("beginTime",exp.getBeginDate() == null ? "" : exp.getBeginDate().toString().substring(11,13));
                    js.put("endTime",exp.getEndDate() == null ? "" : exp.getEndDate().toString().substring(11,13));
                    jsonArray1.add(js);
                }

            }
            json.put("expTimeList",jsonArray);
            json.put("expTimeList1",jsonArray1);
        return new ResponseEntity(new RestResponseEntity(100,"成功", json), HttpStatus.OK);
    }

    /**
     * @Description:网点编辑保存
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    @RequestMapping(value = "/updateExpSchool", method = {RequestMethod.POST})
    public ResponseEntity updateExpSchool(@RequestBody ExpSchoolForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "网点id不能为空", null), HttpStatus.OK);
        if (form.takedAddress == null || StringUtils.isBlank(form.takedAddress))
            return new ResponseEntity(new RestResponseEntity(110, "收件地址不能为空", null), HttpStatus.OK);
        if(StringUtils.isBlank(form.name)){
            return new ResponseEntity(new RestResponseEntity(120,"网点名称不能为空",null), HttpStatus.OK);
        }
        ExpSchool exp = schoolService.selectExpSchool(form.id,form.name);
        if(exp != null){
            return new ResponseEntity(new RestResponseEntity(120,"系统已存在当前网点",null), HttpStatus.OK);
        }
        List<Integer> list = new ArrayList<>();
        for(SendTime time : form.sendTimeList){
            if(time.sendDoor1 == null || time.sendDoor2 == null){
                return new ResponseEntity(new RestResponseEntity(130, "请输入完整的派件上门时间", null), HttpStatus.OK);
            }
            if(time.sendDoor1 == time.sendDoor2){
                return new ResponseEntity(new RestResponseEntity(140, "派件上门的开始时间不能和结束时间一样", null), HttpStatus.OK);
            }
            if(time.sendDoor1 !=null && time.sendDoor2!= null){
                if(time.sendDoor1 <0 || time.sendDoor1 >24 || time.sendDoor2 <0 || time.sendDoor2 >24){
                    return new ResponseEntity(new RestResponseEntity(140, "派件上门的时间输入有误", null), HttpStatus.OK);
                }
                list.add(time.sendDoor1);
                list.add(time.sendDoor2);
            }
        }
        for(int i = 0;i<list.size()-1;i++){
            if(list.get(i) > list.get(i+1)){
                return new ResponseEntity(new RestResponseEntity(150, "派件上门的开始时间不能大于结束时间", null), HttpStatus.OK);
            }
        }

        List<Integer> list1 = new ArrayList<>();
        for(ReceiveTime time : form.receiveTimeList){
            if(time.receiveDoor1 == null || time.receiveDoor2 == null){
                return new ResponseEntity(new RestResponseEntity(130, "请输入完整的上门取件时间", null), HttpStatus.OK);
            }
            if(time.receiveDoor1 == time.receiveDoor2){
                return new ResponseEntity(new RestResponseEntity(140, "上门取件的开始时间不能和结束时间一样", null), HttpStatus.OK);
            }
            if(time.receiveDoor1 !=null && time.receiveDoor2!= null){
                if(time.receiveDoor1 <0 || time.receiveDoor1 >24 || time.receiveDoor2 <0 || time.receiveDoor2 >24){
                    return new ResponseEntity(new RestResponseEntity(140, "上门取件的时间输入有误", null), HttpStatus.OK);
                }
                list1.add(time.receiveDoor1);
                list1.add(time.receiveDoor2);
            }
        }
        for(int i = 0;i<list1.size()-1;i++){
            if(list1.get(i) > list1.get(i+1)){
                return new ResponseEntity(new RestResponseEntity(150, "上门取件的开始时间不能大于结束时间", null), HttpStatus.OK);
            }
        }
        schoolService.updateExpSchool(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:网点新增
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    @RequestMapping(value = "/addExpSchool", method = {RequestMethod.POST})
    public ResponseEntity addExpSchool(@RequestBody ExpSchoolForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
        if(StringUtils.isBlank(form.name)){
            return new ResponseEntity(new RestResponseEntity(120,"网点名称不能为空",null), HttpStatus.OK);
        }
        if(StringUtils.isBlank(form.takedAddress)){
            return new ResponseEntity(new RestResponseEntity(120,"收件地址不能为空",null), HttpStatus.OK);
        }
        ExpSchool exp = schoolService.get2(ExpSchool.class,"name",form.name);
        if(exp != null){
            return new ResponseEntity(new RestResponseEntity(120,"系统已存在当前网点",null), HttpStatus.OK);
        }
        List<Integer> list = new ArrayList<>();
        for(SendTime time : form.sendTimeList){
            if(time.sendDoor1 == null || time.sendDoor2 == null){
                return new ResponseEntity(new RestResponseEntity(130, "请输入完整的派件上门时间", null), HttpStatus.OK);
            }
            if(time.sendDoor1 == time.sendDoor2){
                return new ResponseEntity(new RestResponseEntity(140, "派件上门的开始时间不能和结束时间一样", null), HttpStatus.OK);
            }
            if(time.sendDoor1 !=null && time.sendDoor2!= null){
                if(time.sendDoor1 <0 || time.sendDoor1 >24 || time.sendDoor2 <0 || time.sendDoor2 >24){
                    return new ResponseEntity(new RestResponseEntity(140, "派件上门的时间输入有误", null), HttpStatus.OK);
                }
                list.add(time.sendDoor1);
                list.add(time.sendDoor2);
            }
        }
        for(int i = 0;i<list.size()-1;i++){
            if(list.get(i) > list.get(i+1)){
                return new ResponseEntity(new RestResponseEntity(150, "派件上门的开始时间不能大于结束时间", null), HttpStatus.OK);
            }
        }

        List<Integer> list1 = new ArrayList<>();
        for(ReceiveTime time : form.receiveTimeList){
            if(time.receiveDoor1 == null || time.receiveDoor2 == null){
                return new ResponseEntity(new RestResponseEntity(130, "请输入完整的上门取件时间", null), HttpStatus.OK);
            }
            if(time.receiveDoor1 == time.receiveDoor2){
                return new ResponseEntity(new RestResponseEntity(140, "上门取件的开始时间不能和结束时间一样", null), HttpStatus.OK);
            }
            if(time.receiveDoor1 !=null && time.receiveDoor2!= null){
                if(time.receiveDoor1 <0 || time.receiveDoor1 >24 || time.receiveDoor2 <0 || time.receiveDoor2 >24){
                    return new ResponseEntity(new RestResponseEntity(140, "上门取件的时间输入有误", null), HttpStatus.OK);
                }
                list1.add(time.receiveDoor1);
                list1.add(time.receiveDoor2);
            }
        }
        for(int i = 0;i<list1.size()-1;i++){
            if(list1.get(i) > list1.get(i+1)){
                return new ResponseEntity(new RestResponseEntity(150, "上门取件的开始时间不能大于结束时间", null), HttpStatus.OK);
            }
        }
        ExpSchool ex = schoolService.addExpSchool(form);
        List<Integer> list2 = (List<Integer>) SessionUtils.getSession(request,SessionUtils.SCHOOL_ID);
        list2.add(ex.getId());
        SessionUtils.setSession(response,SessionUtils.SCHOOL_ID,3600 * 7,list);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:网点逻辑删除
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    @RequestMapping(value = "/schoolDelete", method = {RequestMethod.POST})
    public ResponseEntity schoolDelete(@RequestBody IdForm form) throws Exception {
        if(form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "网点id不存在", null), HttpStatus.OK);
        schoolService.schoolDelete(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

}
