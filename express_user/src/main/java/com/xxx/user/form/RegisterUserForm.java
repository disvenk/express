package com.xxx.user.form;

import java.io.Serializable;

public class RegisterUserForm implements Serializable {
    public String userCode;
    public String phoneNumber;
    public String verificationCode;
    public String password;
    public Integer loginType;
}
