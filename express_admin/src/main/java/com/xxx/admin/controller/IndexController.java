package com.xxx.admin.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
import com.xxx.utils.RandomUtils;
import com.xxx.utils.UUIDUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;


@Controller
public class IndexController {

    @RequestMapping(value = "/", method = {RequestMethod.GET})
    public String data_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "home";
    }


    @RequestMapping(value = "/textPageList", method = {RequestMethod.GET})
    public ResponseEntity text(HttpServletRequest request,
                               @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                               @RequestParam(name = "name", required = false) String name) throws Exception {
        if (pageSize >= 100)
            return new ResponseEntity(new RestResponseEntity(110, "条数过长", null), HttpStatus.OK);
        System.out.println(name);
        JSONArray list = new JSONArray();
        for (int i=(pageNum - 1 )* pageSize ; i< (pageNum - 1 )* pageSize + pageSize; i++) {
            JSONObject json = new JSONObject();
            json.put("id",i);
            json.put("name","name"+i);
            json.put("age", 17 + i);
            json.put("uuid", UUIDUtils.generatorUUID32());
            json.put("random", RandomUtils.randomFixedLength(4));
            json.put("status", RandomUtils.randomFixedLength(1));
            json.put("time", DateTimeUtils.parseStr( System.currentTimeMillis() - 6000*7*i));
            json.put("time1",DateTimeUtils.parseStr(new Date()));
            list.add(json);
        }

        return new ResponseEntity(new PageResponseEntity(100, "成功", list, pageNum, pageSize, 100), HttpStatus.OK);
    }


}
