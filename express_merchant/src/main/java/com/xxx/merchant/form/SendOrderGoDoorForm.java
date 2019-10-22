package com.xxx.merchant.form;


import java.io.Serializable;

public class SendOrderGoDoorForm implements Serializable{
    public Integer id;
    public Double weight;
    public Double price;
    public Double todoorFee;
    public Double costPrice;//成本费用
    public Integer deliveryId;
    public String goodsTypeName;
    public String deliveryName;
    public String remark;
    public String sendProvince;
    public String sendCity;
    public String sendZone;
    public String receiveProvince;
    public String receiveCity;
    public String receiveZone;
}
