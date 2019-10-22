package com.xxx.clients.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.clients.service.SchoolService;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.ExpSchool;
import com.xxx.user.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/school")
public class SchoolController {

    @Autowired
    private SchoolService schoolService;

    /**
     * @Description: 学校网点列表
     * @Author: Chen.zm
     * @Date: 2018/1/4 0004
     */
    @RequestMapping(value = "/list", method = {RequestMethod.POST})
    public ResponseEntity list() throws Exception {
        List<ExpSchool> list = schoolService.findSchoolList();
        JSONArray data = new JSONArray();
        for (ExpSchool school : list) {
            JSONObject json = new JSONObject();
            json.put("id", school.getId());
            json.put("name", school.getName());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }


}
