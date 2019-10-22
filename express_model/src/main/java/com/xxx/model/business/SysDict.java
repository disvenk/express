package com.xxx.model.business;

import com.xxx.core.entity.GenericEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * 配置表
 */
@Entity
@Table(name = "sys_dict")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="sys_dict", allocationSize=1)
public class SysDict extends GenericEntity {

	/**
	 *	标签
	 */
	@Column(name = "keytype")
	private String keyType;

	/**
	 *  变量名
	 */
	@Column(name = "keyname")
	private String keyName;

	/**
	 * 变量值
	 */
	@Column(name = "keyvalue")
	private String keyValue;

	/**
	 *	描述
	 */
	@Column(name = "description")
	private String description;

	/**
	 *	排序
	 */
	@Column(name = "sort")
	private Double sort;

	public String getKeyType() {
		return keyType;
	}

	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public String getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getSort() {
		return sort;
	}

	public void setSort(Double sort) {
		this.sort = sort;
	}
}
