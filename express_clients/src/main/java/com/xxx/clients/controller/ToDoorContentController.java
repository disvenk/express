package com.xxx.clients.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.clients.form.IdForm;
import com.xxx.clients.service.ToDoorContentService;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.ExpSchoolWorkTime;
import com.xxx.model.business.ExpServiceFee;
import com.xxx.user.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @Description:
 * @Author: disvenk.dai
 * @Date: 下午 3:40 2018/1/17 0017
 */
@Controller
@RequestMapping("toDoorContent")
public class ToDoorContentController {

    @Autowired
    private ToDoorContentService toDoorContentService;

    /**
     * @Description:网点上门费查询
     * @Author: disvenk.dai
     * @Date: 下午 3:40 2018/1/17 0017
     */
    @RequestMapping(value = "/findToDoorFeeBySchool",method = RequestMethod.POST)
    public ResponseEntity findToDoorFeeBySchool(@RequestBody IdForm form) throws ResponseEntityException {
       ExpServiceFee expServiceFee = toDoorContentService.get2(ExpServiceFee.class,"schoolId", CurrentUser.get().schoolId,"type",form.type);
        if(expServiceFee==null){
            throw new ResponseEntityException(220,"该网店未维护上门费");
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",expServiceFee), HttpStatus.OK);
    }

    /**
     * @Description:网点上门时间
     * @Author: disvenk.dai
     * @Date: 下午 3:40 2018/1/17 0017
     */
    @RequestMapping(value = "/findToDoorTimeBySchool",method = RequestMethod.POST)
    public ResponseEntity findToDoorTimeBySchool(@RequestBody IdForm form){
        List<ExpSchoolWorkTime> list = toDoorContentService.findToDoorTimeBySchool(form.type);//0
        JSONArray jsonArray = new JSONArray();
        for(int i=0;i<list.size();i++){
            if(i==0){
                JSONObject js = new JSONObject();
                String s = list.get(i).getBeginDate() == null ? "" :  list.get(i).getBeginDate().toString().substring(11,13);
                String e =  list.get(i).getEndDate() == null ? "" :  list.get(i).getEndDate().toString().substring(11,13);
                js.put("label",s+"点-"+e+"点");
                js.put("value",s+"点-"+e+"点");
                jsonArray.add(js);
            }else {
                JSONObject js = new JSONObject();
                String s = list.get(i).getBeginDate() == null ? "" :  list.get(i).getBeginDate().toString().substring(11,13);
                String e =  list.get(i).getEndDate() == null ? "" :  list.get(i).getEndDate().toString().substring(11,13);
                js.put("label",s+"点-"+e+"点");
                js.put("value",s+"点-"+e+"点");
                jsonArray.add(js);
            }

        }
        return new ResponseEntity(new RestResponseEntity(100,"成功", jsonArray), HttpStatus.OK);
    }
}
