package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * 寄件订单表
 */
@Entity
@Table(name = "exp_send_order")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="exp_send_order", allocationSize=1)
public class ExpSendOrder extends GenericEntity {

	/**
	 *  网点id
	 */
	@Column(name = "school_id")
	private Integer schoolId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "school_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private ExpSchool expSchool;

	/**
	 *  用户id
	 */
	@Column(name = "user_id")
	private Integer userId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private ExpUser expUser;

	/**
	 *  订单编号
	 */
	@Column(name = "order_no")
	private String orderNo;

	/**
	 *  取件方式    0 自行寄件，   1 上门取件
	 */
	@Column(name = "type")
	private Integer type;

	/**
	 *  订单状态    1 待取件，  2 已取件 3 已支付 4已取消
	 */
	@Column(name = "order_status")
	private Integer orderStatus;

	/**
	 *  订单费用 (快递价格 + 上门小费)
	 */
	@Column(name = "order_price")
	private Double orderPrice;

	/**
	 *  快递价格
	 */
	@Column(name = "price")
	private Double price;

	/**
	 *  上门费
	 */
	@Column(name = "todoor_fee")
	private Double todoorFee;

	/**
	 *  上门小费
	 */
	@Column(name = "todoor_tip_fee")
	private Double todoorTipFee;

	/**
	 *  成本
	 */
	@Column(name = "cost")
	private Double cost;

	/**
	 *  收费状态  0：未支付，    1：已支付
	 */
	@Column(name = "pay_status")
	private Integer payStatus;

	/**
	 *  收件人姓名
	 */
	@Column(name = "receiver_name")
	private String receiverName;

	/**
	 *  收件人手机
	 */
	@Column(name = "receiver_tel")
	private String receiverTel;

	/**
	 * 省
	 */
	@Column(name = "receiver_province")
	private String receiverProvince;

	/**
	 * 市
	 */
	@Column(name = "receiver_city")
	private String receiverCity;

	/**
	 * 区
	 */
	@Column(name = "receiver_zone")
	private String receiverZone;

	/**
	 *  收件人地址
	 */
	@Column(name = "receiver_address")
	private String receiverAddress;

	/**
	 *  寄件人姓名
	 */
	@Column(name = "send_name")
	private String sendName;

	/**
	 *  寄件人手机
	 */
	@Column(name = "send_tel")
	private String sendTel;

	/**
	 *  寄到省id
	 */
	@Column(name = "province_Id")
	private Integer provinceId;

	/**
	 * 省
	 */
	@Column(name = "send_province")
	private String sendProvince;

	/**
	 * 市
	 */
	@Column(name = "send_city")
	private String sendCity;

	/**
	 * 区
	 */
	@Column(name = "send_zone")
	private String sendZone;

	/**
	 *  寄件人地址
	 */
	@Column(name = "send_address")
	private String sendAddress;

	/**
	 *  寄件人地址id
	 */
	@Column(name = "sendAddress_id")
	private String sendAddressId;

	/**
	 *  上门时间
	 */
	@Column(name = "todoor_beginend")
	private String todoorBeginEND;

	/**
	 *	快递公司id
	 */
	@Column(name = "delivery_id")
	private Integer deliveryId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "delivery_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private ExpDeliveryCompany expDeliveryCompany;

	/**
	 *  快递单号
	 */
	@Column(name = "delivery_no")
	private String deliveryNo;

	/**
	 *	上门送件人员ID
	 */
	@Column(name = "deliverer_id")
	private Integer delivererId;

	/*
	* 寄件操作人员ID
	* */
	/*@Column
	private Integer senderId;*/

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "deliverer_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private ExpUser expUserDeliverer;

	/**
	 *	物品类型
	 */
	@Column(name = "goods_type_id")
	private Integer goodsTypeId;

	/**
	 *	物品类型名称
	 */
	@Column(name = "goods_type_name")
	private String goodsTypeName;

	/**
	 *	物品说明
	 */
	@Column(name = "goods_description")
	private String goodsDescription;

	/**
	 *	物品重量
	 */
	@Column(name = "weight")
	private Double weight;

	/**
	 *	取件时间
	 */
	@Column(name = "express_Date")
	private Date expressDate;


	/**
	 *	备注
	 */
	@Column(name = "remarks")
	private String remarks;

	public Double getTodoorTipFee() {
		return todoorTipFee;
	}

	public void setTodoorTipFee(Double todoorTipFee) {
		this.todoorTipFee = todoorTipFee;
	}

	public Integer getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public String getSendAddressId() {
		return sendAddressId;
	}

	public void setSendAddressId(String sendAddressId) {
		this.sendAddressId = sendAddressId;
	}

	public String getReceiverProvince() {
		return receiverProvince;
	}

	public void setReceiverProvince(String receiverProvince) {
		this.receiverProvince = receiverProvince;
	}

	public String getReceiverCity() {
		return receiverCity;
	}

	public void setReceiverCity(String receiverCity) {
		this.receiverCity = receiverCity;
	}

	public String getReceiverZone() {
		return receiverZone;
	}

	public void setReceiverZone(String receiverZone) {
		this.receiverZone = receiverZone;
	}

	public String getSendProvince() {
		return sendProvince;
	}

	public void setSendProvince(String sendProvince) {
		this.sendProvince = sendProvince;
	}

	public String getSendCity() {
		return sendCity;
	}

	public void setSendCity(String sendCity) {
		this.sendCity = sendCity;
	}

	public String getSendZone() {
		return sendZone;
	}

	public void setSendZone(String sendZone) {
		this.sendZone = sendZone;
	}

	public Integer getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(Integer schoolId) {
		this.schoolId = schoolId;
	}

	public ExpSchool getExpSchool() {
		return expSchool;
	}

	public void setExpSchool(ExpSchool expSchool) {
		this.expSchool = expSchool;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public ExpUser getExpUser() {
		return expUser;
	}

	public void setExpUser(ExpUser expUser) {
		this.expUser = expUser;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Double getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(Double orderPrice) {
		this.orderPrice = orderPrice;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getTodoorFee() {
		return todoorFee;
	}

	public void setTodoorFee(Double todoorFee) {
		this.todoorFee = todoorFee;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Integer getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getReceiverTel() {
		return receiverTel;
	}

	public void setReceiverTel(String receiverTel) {
		this.receiverTel = receiverTel;
	}

	public String getReceiverAddress() {
		return receiverAddress;
	}

	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}

	public String getSendName() {
		return sendName;
	}

	public void setSendName(String sendName) {
		this.sendName = sendName;
	}

	public String getSendTel() {
		return sendTel;
	}

	public void setSendTel(String sendTel) {
		this.sendTel = sendTel;
	}

	public String getSendAddress() {
		return sendAddress;
	}

	public void setSendAddress(String sendAddress) {
		this.sendAddress = sendAddress;
	}
	public String getTodoorBeginEND() {
		return todoorBeginEND;
	}

	public void setTodoorBeginEND(String todoorBeginEND) {
		this.todoorBeginEND = todoorBeginEND;
	}

	public Integer getDeliveryId() {
		return deliveryId;
	}

	public void setDeliveryId(Integer deliveryId) {
		this.deliveryId = deliveryId;
	}

	public ExpDeliveryCompany getExpDeliveryCompany() {
		return expDeliveryCompany;
	}

	public void setExpDeliveryCompany(ExpDeliveryCompany expDeliveryCompany) {
		this.expDeliveryCompany = expDeliveryCompany;
	}

	public String getDeliveryNo() {
		return deliveryNo;
	}

	public void setDeliveryNo(String deliveryNo) {
		this.deliveryNo = deliveryNo;
	}

	public Integer getDelivererId() {
		return delivererId;
	}

	public void setDelivererId(Integer delivererId) {
		this.delivererId = delivererId;
	}

	public ExpUser getExpUserDeliverer() {
		return expUserDeliverer;
	}

	public void setExpUserDeliverer(ExpUser expUserDeliverer) {
		this.expUserDeliverer = expUserDeliverer;
	}

	public Integer getGoodsTypeId() {
		return goodsTypeId;
	}

	public void setGoodsTypeId(Integer goodsTypeId) {
		this.goodsTypeId = goodsTypeId;
	}

	public String getGoodsTypeName() {
		return goodsTypeName;
	}

	public void setGoodsTypeName(String goodsTypeName) {
		this.goodsTypeName = goodsTypeName;
	}

	public String getGoodsDescription() {
		return goodsDescription;
	}

	public void setGoodsDescription(String goodsDescription) {
		this.goodsDescription = goodsDescription;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Date getExpressDate() {
		return expressDate;
	}

	public void setExpressDate(Date expressDate) {
		this.expressDate = expressDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
