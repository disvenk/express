package com.xxx.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.ExpReceiveOrder;
import com.xxx.model.business.ExpUser;
import com.xxx.user.cache.VerifyCache;
import com.xxx.user.form.NoticePushForm;
import com.xxx.user.form.SendMessageForm;
import com.xxx.user.form.VerifyCodeForm;
import com.xxx.user.notice.SmsUtils;
import com.xxx.user.service.VerifyService;
import com.xxx.user.service.WeChatService;
import com.xxx.utils.JavaValidate;
import com.xxx.utils.RandomUtils;
import com.xxx.utils.date.DateTimeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 验证管理
 */
@RestController
@RequestMapping("/weChat")
public class WeChatController {

    @Autowired
    private WeChatService weChatService;

    /**
     * @Description: 发送客服消息
     * @Author: Chen.zm
     * @Date: 2018/1/25
     */
    @RequestMapping(value = "/sendMessage", method = {RequestMethod.POST})
    public ResponseEntity sendMessage(@RequestBody SendMessageForm form) throws Exception {
        if (org.apache.commons.lang3.StringUtils.isBlank(form.content))
            return new ResponseEntity(new RestResponseEntity(120, "内容不能为空", null), HttpStatus.OK);
        if (form.userId == null)
            return new ResponseEntity(new RestResponseEntity(110, "用户id不能为空", null), HttpStatus.OK);
        boolean b = weChatService.sendTextMessage(form.userId, form.content);
        JSONObject json = new JSONObject();
        json.put("status", b);
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }



}
