package com.xxx.admin.controller;

import com.xxx.admin.form.UserQueryForm;
import com.xxx.core.response.RestResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    /**
     * @Description:获取用户信息
     * @Author: disvenk.dai
     * @Date: 2018/1/4
     */
    @RequestMapping(value = "/getUserList")
    public ResponseEntity getUserList(@RequestBody UserQueryForm form) {
        return null;
    }
}
