package com.xxx.admin.form;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExpSchoolForm implements Serializable{
    public Integer id;
    public String name;
    public Boolean capture;
    public Integer ispaper;
    public String template;
    public String takedAddress;
//    public SendTime sendTimeList;
    public List<SendTime> sendTimeList;
    public List<ReceiveTime> receiveTimeList;

}
