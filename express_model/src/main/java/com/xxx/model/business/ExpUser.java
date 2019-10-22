package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import com.xxx.model.system.SYS_Role;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.*;

/**
 * 用户表
 */
@Entity
@Table(name = "exp_user")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="exp_user", allocationSize=1)
public class ExpUser extends GenericEntity {


	/**
	 *	账户登录入口
	 * 		1 学生
	 *		2 快递人员
	 *		3 平台用户
	 *		0  超级管理员
	 */
	@Column(name = "logintype")
	private Integer loginType;

	/**
	 * 用户名
	 */
	@Column(name = "user_code")
	private String userCode;

	public String getRelName() {
		return relName;
	}

	public void setRelName(String relName) {
		this.relName = relName;
	}

	/**
	 * 真实名
	 */
	@Column

	private String relName;

	/**
	 * 登录手机号
	 */
	@Column(name = "user_mobile")
	private String userMobile;

	/**
	 * 密码
	 */
	@Column(name = "user_password")
	private String userPassword;

	/**
	 * 昵称
	 */
	@Column(name = "user_nickname")
	private String userNickname;

	/**
	 * 头像
	 */
	@Column(name = "icon")
	private String icon;

	/**
	 * 最后登录时间
	 */
	@Column(name = "final_login_date")
	private Date finalLoginDate;

	/**
	 * 最后一次登录的上一次登录时间
	 */
	@Column(name = "last_login_date")
	private Date lastLoginDate;

	/**
	 * 最后登录ip
	 */
	@Column(name = "final_login_ip")
	private String finalLoginIp;

	/**
	 * 密码修改时间
	 */
	@Column(name = "password_update_date")
	private Date passwordUpdateDate;

	/**
	 *	备注
	 */
	@Column(name = "remarks")
	private String remarks;

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

	//角色集合
	@JSONField(serialize = false)
	@Fetch(FetchMode.SUBSELECT)
	@org.hibernate.annotations.ForeignKey(name = "none")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "EXP_User_Role",
			joinColumns = @JoinColumn(name = "userId", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "roleId", referencedColumnName = "id")
	)
	private Set<SYS_Role> roleList = new HashSet<>();

	public Set<SYS_Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(Set<SYS_Role> roleList) {
		this.roleList = roleList;
	}

	public Integer getLoginType() {
		return loginType;
	}

	public void setLoginType(Integer loginType) {
		this.loginType = loginType;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUserNickname() {
		return userNickname;
	}

	public void setUserNickname(String userNickname) {
		this.userNickname = userNickname;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Date getFinalLoginDate() {
		return finalLoginDate;
	}

	public void setFinalLoginDate(Date finalLoginDate) {
		this.finalLoginDate = finalLoginDate;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public String getFinalLoginIp() {
		return finalLoginIp;
	}

	public void setFinalLoginIp(String finalLoginIp) {
		this.finalLoginIp = finalLoginIp;
	}

	public Date getPasswordUpdateDate() {
		return passwordUpdateDate;
	}

	public void setPasswordUpdateDate(Date passwordUpdateDate) {
		this.passwordUpdateDate = passwordUpdateDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
}
