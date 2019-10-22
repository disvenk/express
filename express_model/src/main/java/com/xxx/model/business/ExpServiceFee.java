package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * 网点上门服务价格表
 */
@Entity
@Table(name = "exp_service_fee")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="exp_service_fee", allocationSize=1)
public class ExpServiceFee extends GenericEntity {

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
	 *  收费类型  1派件上门,  2上门取件
	 */
	@Column(name = "type")
	private Integer type;

	/**
	 *  收费金额
	 */
	@Column(name = "fee")
	private Double fee;

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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}
}
