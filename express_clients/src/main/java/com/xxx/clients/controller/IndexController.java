package com.xxx.clients.controller;


import com.xxx.clients.service.WeChatService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;


@Controller
public class IndexController {



    @RequestMapping(value = "/", method = {RequestMethod.GET})
    public String data_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "home";
    }
}
