package com.xxx.merchant.form;


import java.io.Serializable;

public class SendOrderForm implements Serializable{
    public Integer pageNum;
    public Integer id;
    public String receiverName;
    public String receiverTel;
    public Double weight;
    public Double price;
    public Double costPrice;//成本
    public Integer deliveryId;
    public String deliveryName;
    public String goodsTypeName;
    public Integer type;//0 自行寄件，   1 上门取件
    public Integer status;//1 查询自己抢到的单，   1 上门取件
}
