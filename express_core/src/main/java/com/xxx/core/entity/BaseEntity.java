package com.xxx.core.entity;

import com.xxx.utils.CloneUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public abstract class BaseEntity<T> implements Serializable {
    public BaseEntity() {
    }

    @Id
    @Column(length = 32) //如果是字符串，则 length 会起作用
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "idGenerator")
    private T id;

    @Version
    @Column(nullable = false, columnDefinition = "int default 1")
    private int version = 1;

    @Column(nullable = false, columnDefinition = "int default 1")
    private int appId = 1;

    @Column(nullable = false, columnDefinition = "tinyint(1) default 0")
    private boolean logicDeleted = false;

    @Column(length = 32)
    private T createdBy;

    @Column(length = 32)
    private T createdChannel;

    @Column(nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate = new Date();

    @Column(length = 32)
    private T updateBy;

    @Column(length = 32)
    private T updateChannel;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @Transient
    private boolean dirty;

    //深复制
    @Override
    public Object clone() throws CloneNotSupportedException {
        return CloneUtils.depthClone(this);
    }


    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public boolean isLogicDeleted() {
        return logicDeleted;
    }

    public void setLogicDeleted(boolean logicDeleted) {
        this.logicDeleted = logicDeleted;
    }

    public T getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(T createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public T getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(T updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }


    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public T getCreatedChannel() {
        return createdChannel;
    }

    public void setCreatedChannel(T createdChannel) {
        this.createdChannel = createdChannel;
    }

    public T getUpdateChannel() {
        return updateChannel;
    }

    public void setUpdateChannel(T updateChannel) {
        this.updateChannel = updateChannel;
    }

}
