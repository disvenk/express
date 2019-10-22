package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * 学校网点表
 */
@Entity
@Table(name = "exp_school_worktime")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="exp_school_worktime", allocationSize=1)
public class ExpSchoolWorkTime extends GenericEntity {

	/**
	 * 网点id
	 */
	@Column(name = "school_id")
	private Integer schoolId;

	@JSONField
	@JsonIgnore
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "school_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	private ExpSchool expSchool;

	/**
	 * 时间类型：0 上门取件   1 派件上门
	 */
	@Column(name = "type")
	private Integer type;

	/**
	 * 上门开始时间
	 */
	@Column(name = "begin_date")
	private Date beginDate;

	/**
	 * 上门结束时间
	 */
	@Column(name = "end_date")
	private Date endDate;

	/**
	 * 备注
	 */
	@Column(name = "remarks")
	private String remarks;

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

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
