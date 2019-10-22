package com.xxx.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.ExpUser;
import com.xxx.user.form.SendMessageForm;
import com.xxx.user.notice.SmsUtils;
import com.xxx.user.service.EnterPushNoticeService;
import com.xxx.user.service.SaveSmsInfoService;
import com.xxx.user.service.WeChatService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Description:
 * @Author: disvenk.dai
 * @Date: 下午 2:56 2018/1/26 0026
 */
@Controller
@RequestMapping("sendMessageEnter")
public class EnterPushNoticeController {

    @Autowired
    private EnterPushNoticeService enterPushNoticeService;
    @Autowired
    private WeChatService weChatService;
    @Autowired
    private SaveSmsInfoService saveSmsInfoService;

    /**
     * @Description: 录件发送客服消息
     * @Author: Chen.zm
     * @Date: 2018/1/25
     */
    @RequestMapping(value = "/sendMessageEnter", method = {RequestMethod.POST})
    public ResponseEntity sendMessageEnter(@RequestBody SendMessageForm form) throws Exception {
        if (form.validateCode==null || StringUtils.isBlank(form.validateCode))
            return new ResponseEntity(new RestResponseEntity(120, "验证码不能为空", null), HttpStatus.OK);
        if (form.tel == null || StringUtils.isBlank(form.tel))
            return new ResponseEntity(new RestResponseEntity(110, "手机号不能为空", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        ExpUser expUser = enterPushNoticeService.get2(ExpUser.class,"userMobile",form.tel,"loginType",1);
        boolean b = false;
        String address = "";
        String content = "";
        if(expUser != null){
            address = expUser.getExpSchool() == null ? "" :
                    expUser.getExpSchool().getTakedAddress() == null ? "" : expUser.getExpSchool().getTakedAddress();
            //        String content = "您的快递["+form.deliveryNo+"]已到达学校收发室，请凭验证码["+form.validateCode+"]领取";
            content = "亲,您的快递已经到达" + address +
                    "\n快递公司：" + form.deliveryCompanyName +
                    "\n取件密码：" + form.validateCode +
                    "\n取件地址：" + address +
                    "\n运单号：" + form.deliveryNo +
                    "\n请于48小时内领取，超时将退回。如有任何问题请及时联系" + address + "的童鞋，感谢亲的支持。";

            b = weChatService.sendTextMessage(expUser.getId(),content);

            String result = b?"成功":"失败";
            enterPushNoticeService.saveNotice(expUser.getId(),form.deliveryNo,form.tel,form.deliveryCompanyName,form.validateCode,content,result);

        }

        if (!b) {
            //发送短信
            String result = SmsUtils.sendMessageEnter(form.tel, form.validateCode, address);
            String con = "取件码"+form.validateCode+"，您的快件已到达"+address+"，请于48小时内领取，超期将退回！";
            //保存发送记录
            saveSmsInfoService.saveSmsInfo(expUser == null ? null : expUser.getSchoolId(), form.tel,form.validateCode,
                    content,result.equals("SUCCESS")?"成功":"失败");
            if (result.equals("SUCCESS")) {
                b = true;
            }
        }

        json.put("status", b);
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }
}
