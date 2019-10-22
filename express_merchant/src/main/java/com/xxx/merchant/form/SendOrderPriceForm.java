package com.xxx.merchant.form;


import java.io.Serializable;

public class SendOrderPriceForm implements Serializable{
    public Double weight;
    public Integer deliveryId;
    public Integer id;
    public String deliveryName;
    public String receiverProvince;
    public String goodsTypeName;
    public Integer type;//0 自行寄件，   1 上门取件
}
