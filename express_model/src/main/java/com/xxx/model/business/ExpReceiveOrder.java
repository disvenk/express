package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户收件订单表
 */
@Entity
@Table(name = "exp_receiver_order")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="exp_receive_order", allocationSize=1)
public class ExpReceiveOrder extends GenericEntity {

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
	 *  取件方式    0 自行取件，   1 派件上门
	 */
	@Column(name = "type")
	private Integer type;

	/**
	 *  订单状态    1 未到达,  2 已到达,  3 待接单,  4 已接单,  5 待签收,  6 已签收,  7 已拒收 8 已退回
	 */
	@Column(name = "order_status")
	private Integer orderStatus;

	/**
	 *  订单费用
	 */
	@Column(name = "order_price")
	private Double orderPrice;

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
	 *  收费状态  0：未支付，    1：已支付
	 */
	@Column(name = "pay_status")
	private Integer payStatus;

	/**
	 *  验证码
	 */
	@Column(name = "validate_code")
	private String validateCode;

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
	@Column(name = "province")
	private String province;

	/**
	 * 市
	 */
	@Column(name = "city")
	private String city;

	/**
	 * 区
	 */
	@Column(name = "zone")
	private String zone;

	/**
	 *  收件人地址
	 */
	@Column(name = "receiver_address")
	private String receiverAddress;

	/**
	 *  上门时间
	 */
	@Column(name = "todoor_date")
	private Date todoorDate;

	/**
	 *  派件上门时间段
	 */
	@Column(name = "begin_end_time")
	private String beginEndTime;

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

	/**
	 *	录入人员ID
	 */
	/*@Column(name = "enter_id")
	private Integer enterId;*/

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
	 *	送达时间
	 */
	@Column(name = "reach_date")
	private Date reachDate;

	/**
	 *	到达时间
	 */
	@Column(name = "arrived_date")
	private Date arrivedDate;

	/**
	 *	签收时间(取件时间)
	 */
	@Column(name = "express_Date")
	private Date expressDate;

	/**
	 *	拒收时间
	 */
	@Column(name = "refuse_Date")
	private Date refuseDate;

	/**
	 *	面单照图片key
	 */
	@Column(name = "capture")
	private String capture;

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

	public Date getArrivedDate() {
		return arrivedDate;
	}

	public void setArrivedDate(Date arrivedDate) {
		this.arrivedDate = arrivedDate;
	}

	public Date getRefuseDate() {
		return refuseDate;
	}

	public void setRefuseDate(Date refuseDate) {
		this.refuseDate = refuseDate;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getBeginEndTime() {
		return beginEndTime;
	}

	public void setBeginEndTime(String beginEndTime) {
		this.beginEndTime = beginEndTime;
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

	public Double getTodoorFee() {
		return todoorFee;
	}

	public void setTodoorFee(Double todoorFee) {
		this.todoorFee = todoorFee;
	}

	public Integer getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}

	public String getValidateCode() {
		return validateCode;
	}

	public void setValidateCode(String validateCode) {
		this.validateCode = validateCode;
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

	public Date getTodoorDate() {
		return todoorDate;
	}

	public void setTodoorDate(Date todoorDate) {
		this.todoorDate = todoorDate;
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

	public Date getReachDate() {
		return reachDate;
	}

	public void setReachDate(Date reachDate) {
		this.reachDate = reachDate;
	}

	public Date getExpressDate() {
		return expressDate;
	}

	public void setExpressDate(Date expressDate) {
		this.expressDate = expressDate;
	}

	public String getCapture() {
		return capture;
	}

	public void setCapture(String capture) {
		this.capture = capture;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
