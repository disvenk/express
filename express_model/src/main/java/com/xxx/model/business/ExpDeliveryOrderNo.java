package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * 寄件订单编号预存表
 */
@Entity
@Table(name = "exp_delivery_order_no")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="exp_delivery_order_no", allocationSize=1)
public class ExpDeliveryOrderNo extends GenericEntity {

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

	/**
	 *	快递单编号
	 */
	@Column(name = "delivery_no")
	private String deliveryNo;

	/**
	 *	是否使用
	 */
	@Column(name = "is_used")
	private Boolean isUsed;

	/**
	 *	使用时间
	 */
	@Column(name = "used_date")
	private Date usedDate;

	/**
	 *	备注
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

	public String getDeliveryNo() {
		return deliveryNo;
	}

	public void setDeliveryNo(String deliveryNo) {
		this.deliveryNo = deliveryNo;
	}

	public Boolean getUsed() {
		return isUsed;
	}

	public void setUsed(Boolean used) {
		isUsed = used;
	}

	public Date getUsedDate() {
		return usedDate;
	}

	public void setUsedDate(Date usedDate) {
		this.usedDate = usedDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
