package com.xxx.model.business;

import com.xxx.core.entity.GenericEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * 支付表
 */
@Entity
@Table(name = "sys_payment")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sys_payment", allocationSize=1)
public class Payment extends GenericEntity {
	/**
	 * 支付号
	 */
	@Column(name = "pay_no")
	private String payNo;
	/**
	 * 订单ID
	 */
	@Column(name = "order_id")
	private Integer orderId;

	/**
	 * 订单类型 1-上门派件订单(收件订单)   2-(寄件订单)
	 */
	@Column(name = "order_type")
	private Integer orderType;
	/**
	 * 金额（单位：元）
	 */
	@Column(name = "price")
	private Double price;
	/**
	 * 状态 10 待支付 20 支付成功 -1 支付异常
	 */
	@Column(name = "status")
	private Integer status;

	@Column(name = "create_time")
	private Long createTime;
	@Column(name = "finish_time")
	private Long finishTime;
	/**
	 * 支付渠道, ALIPAY:支付宝; WECHAT:微信; UPMP:银联 CASH:现金
	 */
	@Column(name = "channel")
	private String channel;
	/**
	 * 1:线下支付，0或者null为线上
	 */
	@Column(name = "type")
	private Integer type;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}

	public String getPayNo() {
		return payNo;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getPrice() {
		return price;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return status;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setFinishTime(Long finishTime) {
		this.finishTime = finishTime;
	}

	public Long getFinishTime() {
		return finishTime;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getChannel() {
		return channel;
	}


}
