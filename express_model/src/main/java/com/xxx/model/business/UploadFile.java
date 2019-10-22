package com.xxx.model.business;

import com.xxx.core.entity.GenericEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * 文件管理表
 */
@Entity
@Table(name = "upload_file")
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="upload_file", allocationSize=1)
public class UploadFile extends GenericEntity {

	/**
	 * 名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 * 文件类型 1：图片   2：视频
	 */
	@Column(name = "file_type")
	private Integer fileType = 1;

	/**
	 * 存储方式  1-阿里云0SS存储
	 */
	@Column(name = "upload_type")
	private Integer uploadType = 1;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getUploadType() {
		return uploadType;
	}

	public void setUploadType(Integer uploadType) {
		this.uploadType = uploadType;
	}

	public Integer getFileType() {
		return fileType;
	}

	public void setFileType(Integer fileType) {
		this.fileType = fileType;
	}
}
