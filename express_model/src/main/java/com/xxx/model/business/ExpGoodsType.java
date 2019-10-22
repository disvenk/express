package com.xxx.model.business;

import com.xxx.core.entity.GenericEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * 物品类型表
 */
@Entity
@Table(name = "exp_goods_type")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="exp_goods_type", allocationSize=1)
public class ExpGoodsType extends GenericEntity {

	/**
	 *  类型名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 *	备注
	 */
	@Column(name = "remarks")
	private String remarks;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
