package com.xxx.user.controller;

import com.xxx.core.response.RestResponseEntity;
import com.xxx.user.utils.SessionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 会话存储
 */
@RestController
@RequestMapping("/session")
public class SessionController {

    @RequestMapping(value = "/setData", method = {RequestMethod.POST})
    public ResponseEntity placeOrderSetData(HttpServletResponse response, @RequestBody String json) throws Exception {
        if (json.length() > 1024 * 7)
            return new ResponseEntity(new RestResponseEntity(110, "内容过长", null), HttpStatus.OK);
        SessionUtils.setSession(response, SessionUtils.DATA, 300, json);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    @RequestMapping(value = "/getData", method = {RequestMethod.POST})
    public ResponseEntity placeOrderDataRes(HttpServletRequest request) throws Exception {
        Object json = SessionUtils.getSession(request, SessionUtils.DATA);
        return new ResponseEntity(new RestResponseEntity(100,"成功", json), HttpStatus.OK);
    }

}
