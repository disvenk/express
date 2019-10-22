package com.xxx.admin.form;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExpDeliveryCompanyListForm implements Serializable{
    public Integer id;
    public Integer schoolId;
    public Integer deliveryId;
    public Integer schoolDeliveryCompanyId;
    public List<ExpDeliveryCompanyDetailForm> expDeliveryCompanyList = new ArrayList<>();
    public String provinceName;
    public Double firstWeightFee;
    public Double otherWeightFee;
    public Double firstCostFee;
    public Double otherCostFee;
    public Double value;
    public String field;
}
