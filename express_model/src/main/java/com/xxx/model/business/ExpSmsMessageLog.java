package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 用户短信推送表
 */
@Entity
@Table(name = "exp_sms_message_log")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="exp_sms_message_log", allocationSize=1)
public class ExpSmsMessageLog extends GenericEntity {

	/**
	 *  网点id
	 */
	@Column(name = "school_id")
	private Integer schoolId;

//	@JSONField
//	@JsonIgnore
//	@NotFound(action= NotFoundAction.IGNORE)
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "school_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
//	private ExpSchool expSchool;

	/**
	 *  用户手机号
	 */
	@Column(name = "user_mobile")
	private String userMobile;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_mobile", referencedColumnName="user_mobile", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private ExpUser expUser;

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

	public Integer getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(Integer schoolId) {
		this.schoolId = schoolId;
	}

//	public ExpSchool getExpSchool() {
//		return expSchool;
//	}
//
//	public void setExpSchool(ExpSchool expSchool) {
//		this.expSchool = expSchool;
//	}

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

	public ExpUser getExpUser() {
		return expUser;
	}

	public void setExpUser(ExpUser expUser) {
		this.expUser = expUser;
	}
}
