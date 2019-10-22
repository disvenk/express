package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 站内信表
 */
@Entity
@Table(name = "exp_notice_message_log")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="exp_notice_message_log", allocationSize=1)
public class ExpNoticeMessageLog extends GenericEntity {


	/**
	 *  通知类型   1 到件通知,  2 取件通知
	 */
	@Column(name = "notice_type")
	private Integer noticeType;

	/**
	 *  是否查看
	 */
	@Column(name = "notice_checked")
	private Boolean noticeChecked;

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
	 *  用户手机号
	 */
	@Column(name = "user_mobile")
	private String userMobile;

	/**
	 *  验证码
	 */
	@Column(name = "validate_code")
	private String validateCode;

	/**
	 *  推送内容
	 */
	@Column(name = "send_content")
	private String sendContent;

	/**
	 *  推送结果
	 */
	@Column(name = "send_result")
	private String sendResult;

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
	 *	订单id
	 */
	@Column(name = "order_id")
	private Integer orderId;

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

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public String getValidateCode() {
		return validateCode;
	}

	public void setValidateCode(String validateCode) {
		this.validateCode = validateCode;
	}

	public String getSendContent() {
		return sendContent;
	}

	public void setSendContent(String sendContent) {
		this.sendContent = sendContent;
	}

	public String getSendResult() {
		return sendResult;
	}

	public void setSendResult(String sendResult) {
		this.sendResult = sendResult;
	}

	public Integer getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(Integer noticeType) {
		this.noticeType = noticeType;
	}

	public Boolean getNoticeChecked() {
		return noticeChecked;
	}

	public void setNoticeChecked(Boolean noticeChecked) {
		this.noticeChecked = noticeChecked;
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

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
}
