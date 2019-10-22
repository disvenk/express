package com.xxx.model.business;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

/**
 * 省份计价表
 */
@Entity
@Table(name = "exp_province_service_fee")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="exp_province_service_fee", allocationSize=1)
public class ExpProvinceServiceFee extends GenericEntity {

	/**
	 *  网点与快递公司的关系表id
	 */
	@Column(name = "school_delivery_company_id")
	private Integer schoolDeliveryCompanyId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "school_delivery_company_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private ExpSchoolDeliveryCompany expSchoolDeliveryCompany;

	/**
	 *	省份id
	 */
	@Column(name = "province_id")
	private Integer provinceId;

	@JSONField
	@JsonIgnore
	@NotFound(action= NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "province_id", insertable = false, updatable = false,foreignKey = @ForeignKey(name="none",value = ConstraintMode.NO_CONSTRAINT))
	private ExpArea expArea;

	/**
	 *	省份名称
	 */
	@Column(name = "province_name")
	private String provinceName;

	/**
	 *	寄件首重价
	 */
	@Column(name = "first_weight_fee")
	private Double firstWeightFee;

	/**
	 *	寄件续重价
	 */
	@Column(name = "other_weight_fee")
	private Double otherWeightFee;


	/**
	 *	成本首重价
	 */
	@Column(name = "first_cost_fee")
	private Double firstCostFee;

	/**
	 *	成本续重价
	 */
	@Column(name = "other_cost_fee")
	private Double otherCostFee;

	/**
	 *	备注
	 */
	@Column(name = "remarks")
	private String remarks;

	public Integer getSchoolDeliveryCompanyId() {
		return schoolDeliveryCompanyId;
	}

	public void setSchoolDeliveryCompanyId(Integer schoolDeliveryCompanyId) {
		this.schoolDeliveryCompanyId = schoolDeliveryCompanyId;
	}

	public ExpSchoolDeliveryCompany getExpSchoolDeliveryCompany() {
		return expSchoolDeliveryCompany;
	}

	public void setExpSchoolDeliveryCompany(ExpSchoolDeliveryCompany expSchoolDeliveryCompany) {
		this.expSchoolDeliveryCompany = expSchoolDeliveryCompany;
	}

	public Integer getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public ExpArea getExpArea() {
		return expArea;
	}

	public void setExpArea(ExpArea expArea) {
		this.expArea = expArea;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public Double getFirstWeightFee() {
		return firstWeightFee;
	}

	public void setFirstWeightFee(Double firstWeightFee) {
		this.firstWeightFee = firstWeightFee;
	}

	public Double getOtherWeightFee() {
		return otherWeightFee;
	}

	public void setOtherWeightFee(Double otherWeightFee) {
		this.otherWeightFee = otherWeightFee;
	}

	public Double getFirstCostFee() {
		return firstCostFee;
	}

	public void setFirstCostFee(Double firstCostFee) {
		this.firstCostFee = firstCostFee;
	}

	public Double getOtherCostFee() {
		return otherCostFee;
	}

	public void setOtherCostFee(Double otherCostFee) {
		this.otherCostFee = otherCostFee;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
