package com.xxx.user.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.ExpArea;
import com.xxx.model.business.ExpDeliveryCompany;
import com.xxx.model.business.ExpSchoolWorkTime;
import com.xxx.user.form.AreaCodeForm;
import com.xxx.user.form.ExpressForm;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.service.ExpressService;
import com.xxx.utils.KuaiDi100;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 快递
 */
@RestController
@RequestMapping("/express")
public class ExpressController {

    @Autowired
    private ExpressService expressService;

    /**
     * @Description: 获取快递信息
     * @Author: Chen.zm
     * @Date: 2017/11/9 0009
     */
    @RequestMapping(value = "/searchExpress", method = {RequestMethod.POST})
    public ResponseEntity searchExpress(@RequestBody ExpressForm form) throws Exception {
        if (StringUtils.isBlank(form.com))
            return new ResponseEntity(new RestResponseEntity(110, "快递公司不能为空", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.nu))
            return new ResponseEntity(new RestResponseEntity(120, "快递单号不能为空", null), HttpStatus.OK);
        String result = KuaiDi100.searchkuaiDiInfo(form.com, form.nu);
        JSONObject data = new JSONObject();
        data.put("result", result);
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }


    /**
     * @Description: 快递公司列表
     * @Author: Chen.zm
     * @Date: 2018/1/8 0008
     */
    @RequestMapping(value = "/list", method = {RequestMethod.POST})
    public ResponseEntity list() throws Exception {
        List<ExpDeliveryCompany> list = expressService.findList();
        JSONArray data = new JSONArray();
        for (ExpDeliveryCompany deliveryCompany : list) {
            JSONObject json = new JSONObject();
            json.put("id", deliveryCompany.getId());
            json.put("shortName",deliveryCompany.getShortName());
            json.put("name", deliveryCompany.getName());
            json.put("com", deliveryCompany.getCom());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }

    /**
     * @Description: 所有省份
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @RequestMapping(value = "/findProvince", method = {RequestMethod.GET})
    public ResponseEntity findProvince() throws Exception {
        List<ExpArea> list = expressService.findProvince();
        JSONArray data = new JSONArray();
        for (ExpArea expArea : list) {
            JSONObject json = new JSONObject();
            json.put("id", expArea.getId());
            json.put("code", expArea.getCode());
            json.put("name", expArea.getName());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }

    /**
     * @Description: 获得省，市，区的联动
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @RequestMapping(value = "/findCity", method = {RequestMethod.GET})
    public ResponseEntity findArea(HttpServletRequest request,
                                   @RequestParam(name = "code", required = false) String code) throws Exception {
        if (StringUtils.isBlank(code))
            return new ResponseEntity(new RestResponseEntity(110, "编码不能为空", null), HttpStatus.OK);
        List<ExpArea> list = expressService.findArea(code);
        JSONArray data = new JSONArray();
        for (ExpArea expArea : list) {
            JSONObject json = new JSONObject();
            json.put("id", expArea.getId());
            json.put("code", expArea.getCode());
            json.put("name", expArea.getName());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }

    /**
     * @Description: 获得所有省市区
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @RequestMapping(value = "/findProvinceCityZone", method = {RequestMethod.POST})
    public ResponseEntity findProvinceCityZone() throws Exception {
        JSONArray data = expressService.findProvinceCityZone();
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }

    /**
     * @Description: 各个网点快递员上门取件时间列表
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @RequestMapping(value = "/getDeliveryTimeFromStudent", method = {RequestMethod.POST})
    public ResponseEntity getDeliveryTimeFromStudent(@RequestBody AreaCodeForm form) throws Exception {
        List<ExpSchoolWorkTime> list = expressService.getDeliveryTimeFromStudent(CurrentUser.get().schoolId);
        JSONArray data = new JSONArray();
        for (ExpSchoolWorkTime time : list) {
            JSONObject json = new JSONObject();
            json.put("id", time.getId());
            json.put("beginDate", time.getBeginDate());
            json.put("endDate", time.getEndDate());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }

    /**
     * @Description: 各个网点快递员 送件上门 时间列表
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @RequestMapping(value = "/sendTimeFromDeliveryer", method = {RequestMethod.POST})
    public ResponseEntity sendTimeFromDeliveryer(@RequestBody AreaCodeForm form) throws Exception {
        List<ExpSchoolWorkTime> list = expressService.sendTimeFromDeliveryer(CurrentUser.get().schoolId);
        JSONArray data = new JSONArray();
        for (ExpSchoolWorkTime time : list) {
            JSONObject json = new JSONObject();
            json.put("id", time.getId());
            json.put("beginDate", time.getBeginDate());
            json.put("endDate", time.getEndDate());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }

    /**
     * @Description: 根据网点查询快递公司
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @RequestMapping(value = "/findCompayBySchool", method = {RequestMethod.POST})
    public ResponseEntity findCompayBySchool() throws Exception {
        List<ExpDeliveryCompany> list =  expressService.findCompayBySchool(CurrentUser.get().schoolId);
        JSONArray data = new JSONArray();
        if(list!=null && !list.isEmpty()){
            for(ExpDeliveryCompany expDeliveryCompany : list){
                JSONObject json = new JSONObject();
                json.put("id",expDeliveryCompany.getId());
                json.put("shortName",expDeliveryCompany.getShortName());
                json.put("name",expDeliveryCompany.getName());
                json.put("com",expDeliveryCompany.getCom());
                data.add(json);
            }
        }


        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }


}
