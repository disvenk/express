package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 网点与快递公司的关系表
 */
@Entity
@Table(name = "exp_school_delivery_company")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="exp_school_delivery_company", allocationSize=1)
public class ExpSchoolDeliveryCompany extends GenericEntity {

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
}
