package com.xxx.model.business;

import com.xxx.core.entity.GenericEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * 省市区基本资料
 */
@Entity
@Table(name = "exp_area")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="exp_area", allocationSize=1)
public class ExpArea extends GenericEntity {

	/**
	 *	区域编码
	 */
	@Column(name = "code")
	private String code;

	/**
	 *  名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 *  类型  0：国家  1：省，直辖市  2：市   3：区县
	 */
	@Column(name = "type")
	private Integer type;

	/**
	 *	区域父编码
	 */
	@Column(name = "parent_code")
	private String parentCode;

	/**
	 *	排序
	 */
	@Column(name = "sort")
	private Double sort;

	/**
	 *	描述
	 */
	@Column(name = "description")
	private String description;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public Double getSort() {
		return sort;
	}

	public void setSort(Double sort) {
		this.sort = sort;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
