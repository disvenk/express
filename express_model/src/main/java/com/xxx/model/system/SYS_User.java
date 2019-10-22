package com.xxx.model.system;

import com.alibaba.fastjson.annotation.JSONField;
import com.xxx.core.entity.GenericEntity;
import com.xxx.model.enumeration.Sex;
import com.xxx.model.enumeration.Status;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="SYS_User", allocationSize=1)
public class SYS_User extends GenericEntity {

    /**
     * 用户名
     */
    @Column(nullable = false, unique = true, length = 30)
    private String account;

    /**
     * 用户密码
     */
    @JSONField(serialize = false)
    @Column(length = 60)
    private String password;

    /**
     * 用户状态
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(length = 50)
    private Status status;

    /**
     * 用户昵称
     */
    @Column(unique = true, length = 30)
    private String nick;

    /**
     * 用户头像 外键(ygf_upload_file)
     */
    private Long headPortraitsId;

    /**
     * 性别
     */
    @Enumerated(EnumType.ORDINAL)
    private Sex sex;

    /**
     * 是否在职
     */
    private Boolean isOnJob;

    /**
     * 出生日期
     */
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    /**
     * 手机号
     */
    @Column(length = 15)
    private String phone;

    /**
     * 邮箱
     */
    @Column(length = 30)
    private String email;

    /**
     * 是否体验账户
     */
    private Boolean experience;

    /**
     * 体验开始日期
     */
    private Boolean experienceStartDate;

    /**
     * 体验结束日期
     */
    private Boolean experienceEndDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 迁移前的表【迁移完成后，隔个几个月再删除此字段】
     */
    private String sourceTable;

    /**
     * 迁移前表中的id【迁移完成后，隔个几个月再删除此字段】
     */
    private Long sourceId;

    //角色集合
    @JSONField(serialize = false)
    @org.hibernate.annotations.ForeignKey(name = "none")
    @ManyToMany
    @JoinTable(
            name = "SYS_User_Role",
            joinColumns = @JoinColumn(name = "userId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "roleId", referencedColumnName = "id")
    )
    private List<SYS_Role> roleList = new ArrayList();


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public Long getHeadPortraitsId() {
        return headPortraitsId;
    }

    public void setHeadPortraitsId(Long headPortraitsId) {
        this.headPortraitsId = headPortraitsId;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public Boolean getOnJob() {
        return isOnJob;
    }

    public void setOnJob(Boolean onJob) {
        isOnJob = onJob;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getExperience() {
        return experience;
    }

    public void setExperience(Boolean experience) {
        this.experience = experience;
    }

    public Boolean getExperienceStartDate() {
        return experienceStartDate;
    }

    public void setExperienceStartDate(Boolean experienceStartDate) {
        this.experienceStartDate = experienceStartDate;
    }

    public Boolean getExperienceEndDate() {
        return experienceEndDate;
    }

    public void setExperienceEndDate(Boolean experienceEndDate) {
        this.experienceEndDate = experienceEndDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public List<SYS_Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<SYS_Role> roleList) {
        this.roleList = roleList;
    }
}