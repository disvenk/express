package com.xxx.model.business;

import com.xxx.core.entity.GenericEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * 第三方登录表
 */
@Entity
@Table(name = "exp_social")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="exp_social", allocationSize=1)
public class ExpSocial extends GenericEntity {

	/**
	 * 说明  微信："WECHAT"
	 */
	@Column(name = "location")
	private String location;

	/**
	 * 第三方唯一标识
	 */
	@Column(name = "uid")
	private String uid;

	/**
	 * 用户id
	 */
	@Column(name = "user_id")
	private Integer userId;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
}
