package com.xxx.clients.controller;


import com.alibaba.fastjson.JSONObject;
import com.xxx.clients.service.UserHomeService;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.user.form.IdForm;
import com.xxx.user.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/userHome")
public class UserHomeController {

    @Autowired
    private UserHomeService userHomeService;

    /**
     * @Description: 首页数据
     * @Author: Chen.zm
     * @Date: 2018/1/4 0004
     */
    @RequestMapping(value = "/info", method = {RequestMethod.POST})
    public ResponseEntity info() throws Exception {
        JSONObject json = userHomeService.findHomeInfo(CurrentUser.get().userId);
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }


}
