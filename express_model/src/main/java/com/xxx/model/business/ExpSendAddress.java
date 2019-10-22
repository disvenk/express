package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户寄件地址
 */
@Entity
@Table(name = "exp_send_address")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="exp_send_address", allocationSize=1)
public class ExpSendAddress extends GenericEntity {


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
	 * 名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 * 手机号
	 */
	@Column(name = "tel")
	private String tel;

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
	 * 详细地址
	 */
	@Column(name = "address")
	private String address;

	/**
	 * 是否默认
	 */
	@Column(name = "is_default")
	private Boolean isDefault;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Boolean getDefault() {
		return isDefault;
	}

	public void setDefault(Boolean aDefault) {
		isDefault = aDefault;
	}
}
