package com.xxx.clients.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.clients.form.IdForm;
import com.xxx.clients.service.NoticeMessageService;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.ExpNoticeMessageLog;
import com.xxx.model.business.ExpSchool;
import com.xxx.user.Commo;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/noticeMessage")
public class NoticeMessageController {

    @Autowired
    private NoticeMessageService noticeMessageService;

    /**
     * @Description: 站内信列表
     * @Author: Chen.zm
     * @Date: 2018/1/4 0004
     */
    @RequestMapping(value = "/list", method = {RequestMethod.POST})
    public ResponseEntity list() throws Exception {
        List<ExpNoticeMessageLog> list = noticeMessageService.findNoticeMessageLogList(CurrentUser.get().userId, CurrentUser.get().schoolId);
        JSONArray data = new JSONArray();
        for (ExpNoticeMessageLog log : list) {
            JSONObject json = new JSONObject();
            json.put("id", log.getId());
            json.put("noticeType", log.getNoticeType());
            json.put("noticeTypeName", Commo.parseNoticeType(log.getNoticeType()));
            json.put("noticeChecked", log.getNoticeChecked());
            json.put("sendContent", log.getSendContent());
            json.put("createTime", DateTimeUtils.parseStr(log.getCreatedDate(), "yyyy-MM-dd hh:mm"));
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }


    /**
     * @Description: 站内信详情
     * @Author: Chen.zm
     * @Date: 2018/1/4 0004
     */
    @RequestMapping(value = "/detail", method = {RequestMethod.POST})
    public ResponseEntity detail(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        ExpNoticeMessageLog log = noticeMessageService.get2(ExpNoticeMessageLog.class, "id", form.id, "userId", CurrentUser.get().userId);
        if (log == null)
            return new ResponseEntity(new RestResponseEntity(120, "站内信不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("noticeType", log.getNoticeType());
        json.put("noticeTypeName", Commo.parseNoticeType(log.getNoticeType()));
        json.put("sendContent", log.getSendContent());
        json.put("createTime", DateTimeUtils.parseStr(log.getCreatedDate(), "yyyy-MM-dd hh:mm"));

        //标记站内信息为已读
        noticeMessageService.updateNoticeMessageLogeChecked(log);
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }


}
