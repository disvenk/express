package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import com.xxx.model.system.SYS_Role;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 学校网点表
 */
@Entity
@Table(name = "exp_school")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="exp_school", allocationSize=1)
public class ExpSchool extends GenericEntity {

	/**
	 * 网点名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 * 是否面单拍照
	 */
	@Column(name = "capture")
	private Boolean capture;

	/**
	 * 面单类型    0 电子， 1 纸质
	 */
	@Column(name = "ispaper")
	private Integer ispaper;

	/**
	 * 上门开始时间
	 */
	@Column(name = "begin_date")
	private Date beginDate;

	/**
	 * 上门结束时间
	 */
	@Column(name = "end_date")
	private Date ednDate;

	/**
	 * 站内信模板
	 */
	@Column(name = "template")
	private String template;

	/**
	 * 取件地址
	 */
	@Column(name="taked_address")
	private String takedAddress;


	//角色集合
	@JSONField(serialize = false)
	@ManyToMany(mappedBy = "expSchools",fetch = FetchType.LAZY)
	@org.hibernate.annotations.ForeignKey(name = "none")
	private List<SYS_Role> roleList = new ArrayList<>();

	public List<SYS_Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<SYS_Role> roleList) {
		this.roleList = roleList;
	}

	public String getTakedAddress() {
		return takedAddress;
	}

	public void setTakedAddress(String takedAddress) {
		this.takedAddress = takedAddress;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getCapture() {
		return capture;
	}

	public void setCapture(Boolean capture) {
		this.capture = capture;
	}

	public Integer getIspaper() {
		return ispaper;
	}

	public void setIspaper(Integer ispaper) {
		this.ispaper = ispaper;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEdnDate() {
		return ednDate;
	}

	public void setEdnDate(Date ednDate) {
		this.ednDate = ednDate;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
}
