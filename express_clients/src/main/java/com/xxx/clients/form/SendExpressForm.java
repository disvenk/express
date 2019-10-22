package com.xxx.clients.form;

public class SendExpressForm {
    public Integer id;//订单id，初始下单的时候不用携带

    public Integer sendPid;//寄件地址id，下单时必填

    public Integer recievePid;//收件地址id，下单时必填

    public Integer goodsTypeId;//物品类型id，下单时必填

    public String doorTime;//上门时间，下单时必填

    public String doorPid;//上门人员id，初始下单不必填

    public Integer expressCompanyId;//快递公司id，下单时必填

    public Integer weight;//重量，不必

    public Integer sendType;//寄件类型，下单时必填,0是自行寄件，1是上门取件

    public String desc;//备注，不必填
}
