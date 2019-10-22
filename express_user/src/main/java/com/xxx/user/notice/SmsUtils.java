package com.xxx.user.notice;


public class SmsUtils {
    /**
     * 发送用于验证码
     */
    public static String sendResetPasswordValidateCode(String phone, String validateCode) {
        String result= com.xxx.utils.SmsUtils.send(phone, "SMS_121085084", "{\"code\":\""+validateCode+"\"}");
        return result;
    }

    /**
     * 发送用于到件通知
     */
    public static String sendMessageEnter(String phone, String validateCode, String name) {
        String result= com.xxx.utils.SmsUtils.send(phone, "SMS_125116005",
                "{\"code\":\""+validateCode+"\",\"name\":\"" + name + "\"}");
        return result;
    }


    public static void main(String[] args){
        sendResetPasswordValidateCode("17715580265", "6666");
    }
}
