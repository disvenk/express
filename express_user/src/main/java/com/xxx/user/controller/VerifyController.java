package com.xxx.user.controller;

import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.ExpSmsMessageLog;
import com.xxx.user.cache.VerifyCache;
import com.xxx.user.form.VerifyCodeForm;
import com.xxx.user.notice.SmsUtils;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.service.SaveSmsInfoService;
import com.xxx.user.service.VerifyService;
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
@RequestMapping("/verify")
public class VerifyController {

    @Autowired
    private VerifyService verifyService;
    @Autowired
    private SaveSmsInfoService saveSmsInfoService;

    /**
     * 获取手机验证码
     * @param request
     * @param verifyCodeForm
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/verificationCode", method = {RequestMethod.POST})
    public ResponseEntity verificationCode(HttpServletRequest request, @RequestBody VerifyCodeForm verifyCodeForm) throws Exception {
        if (verifyCodeForm == null || StringUtils.isBlank(verifyCodeForm.phonenumber))
            return new ResponseEntity(new RestResponseEntity(110, "手机号不能为空", null), HttpStatus.OK);
        if (!JavaValidate.isMobileNO(verifyCodeForm.phonenumber))
            return new ResponseEntity(new RestResponseEntity(120, "手机号格式不正确", null), HttpStatus.OK);
        Date vtime = VerifyCache.getVerificationCodeTime(verifyCodeForm.phonenumber);
        if (vtime != null && DateTimeUtil.getTimeInterval(vtime, new Date()) / 1000d <= 60)
            return new ResponseEntity(new RestResponseEntity(130, "操作太频繁，请于1分钟后重试", null), HttpStatus.OK);
        String validateCode = RandomUtils.randomFixedLength(4);
        VerifyCache.putVerificationCode(verifyCodeForm.phonenumber, validateCode);
        String result = SmsUtils.sendResetPasswordValidateCode(verifyCodeForm.phonenumber, validateCode);
        String content = "【捷杰送达】 验证码"+validateCode+", 您正在进行身份验证，打死不要告诉别人哦！";
        //保存发送记录
        saveSmsInfoService.saveSmsInfo(null,verifyCodeForm.phonenumber,validateCode,
               content,result.equals("SUCCESS")?"成功":"失败");
        if (result.equals("SUCCESS")) {
            return new ResponseEntity(new RestResponseEntity(100, "发送成功", null), HttpStatus.OK);
        } else {
            return new ResponseEntity(new RestResponseEntity(140, "短信发送频率较高，请与24小时后再试", null), HttpStatus.OK);
        }
    }


    /**
     * @Description: 校验验证码是否正确
     * @Author: Chen.zm
     * @Date: 2017/11/13 0013
     */
    @RequestMapping(value = "/checkVerificationCode", method = {RequestMethod.POST})
    public ResponseEntity checkVerificationCode(@RequestBody VerifyCodeForm form) throws Exception {
        if (form == null || StringUtils.isBlank(form.phonenumber))
            return new ResponseEntity(new RestResponseEntity(110, "手机号不能为空", null), HttpStatus.OK);
        if (!JavaValidate.isMobileNO(form.phonenumber))
            return new ResponseEntity(new RestResponseEntity(120, "手机号格式不正确", null), HttpStatus.OK);
        verifyService.checkVerificationCode(form.phonenumber, form.verificationCode);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

}
