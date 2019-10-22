package com.xxx.model.business;

import com.xxx.core.entity.GenericEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * 快递公司表
 */
@Entity
@Table(name = "exp_delivery_company")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="exp_delivery_company", allocationSize=1)
public class ExpDeliveryCompany extends GenericEntity {

	/**
	 *  名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 *	快递com
	 */
	@Column(name = "com")
	private String com;

	/**
	 *	简称
	 */
	@Column(name = "short_name")
	private String shortName;

	/**
	 *	备注
	 */
	@Column(name = "remarks")
	private String remarks;
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCom() {
		return com;
	}

	public void setCom(String com) {
		this.com = com;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
