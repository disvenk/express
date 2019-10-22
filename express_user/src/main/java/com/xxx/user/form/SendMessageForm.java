package com.xxx.user.form;

import java.io.Serializable;

public class SendMessageForm implements Serializable {
    public Integer userId;
    public String content;
    public String tel;
    public String deliveryNo;
    public String deliveryCompanyName;
    public String validateCode;
}