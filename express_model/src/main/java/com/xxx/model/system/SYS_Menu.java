package com.xxx.model.system;

import com.alibaba.fastjson.annotation.JSONField;
import com.xxx.core.entity.GenericEntity;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="SYS_Menu", allocationSize=1)
public class SYS_Menu extends GenericEntity{

    /**
     * 父菜单id
     */
    @Column(name="parent_id")
    private Integer parentId;

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /**
     * 父菜单
     */
    @JSONField(serialize = false)
    @ManyToOne
    @JoinColumn(name = "parent_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private SYS_Menu parent;

    /**
     * 菜单名称
     */
    @Column(nullable = false, length = 30)
    private String name;

    /**
     * 排序
     */
    @Column(columnDefinition = "double(5,3)")
    private Double sort;

    /**
     * 地址
     */
    private String href;

    /**
     * 链接地址打开的目标窗口，默认：mainFrame
     */
    private String target;

    /**
     * 图标
     */
    private String icon;

    /**
     * 备注
     */
    private String remark;

    /**
     * 子菜单集合
     */
    @JSONField(serialize = false)
    @Fetch(FetchMode.SUBSELECT)
    @Where(clause = "logicDeleted=0")
    @OneToMany(mappedBy = "parent")
    @org.hibernate.annotations.ForeignKey(name = "none")
    private Set<SYS_Menu> children = new HashSet<>();

    //角色集合
    @JSONField(serialize = false)
    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(mappedBy = "menuList",fetch = FetchType.LAZY)
    @org.hibernate.annotations.ForeignKey(name = "none")
    private Set<SYS_Role> roleList = new HashSet<>();

    public Set<SYS_Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(Set<SYS_Role> roleList) {
        this.roleList = roleList;
    }

    public Double getSort() {
        return sort;
    }

    public void setSort(Double sort) {
        this.sort = sort;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public SYS_Menu getParent() {
        return parent;
    }

    public void setParent(SYS_Menu parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public  Set<SYS_Menu> getChildren() {
        return children;
    }

    public void setChildren( Set<SYS_Menu> children) {
        this.children = children;
    }



}
