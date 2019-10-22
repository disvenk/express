package com.xxx.model.system;

import com.alibaba.fastjson.annotation.JSONField;
import com.xxx.core.entity.GenericEntity;
import com.xxx.model.business.ExpSchool;
import com.xxx.model.business.ExpUser;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@TableGenerator(name="idGenerator",table="SYS_Table_Generator",pkColumnName = "name",valueColumnName = "value",pkColumnValue="SYS_Role", allocationSize=1)
public class SYS_Role extends GenericEntity {

    /**
     * 角色名称
     */
    @Column(nullable = false, unique = true, length = 30)
    private String name;

    /**
     * 备注
     */
    private String remark;

    //用户集合
    @JSONField(serialize = false)
    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(mappedBy = "roleList",fetch = FetchType.LAZY)
    @org.hibernate.annotations.ForeignKey(name = "none")
    private Set<ExpUser> userList = new HashSet<>();

    //菜单集合
    @JSONField(serialize = false)
    @Fetch(FetchMode.SUBSELECT)
    @org.hibernate.annotations.ForeignKey(name = "none")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "SYS_Role_Menu",
            joinColumns = @JoinColumn(name = "roleId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "menuId", referencedColumnName = "id")
    )
    private Set<SYS_Menu> menuList = new HashSet<>();


    //网点集合

    @JSONField(serialize = false)
    @Fetch(FetchMode.SUBSELECT)
    @org.hibernate.annotations.ForeignKey(name = "none")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "SYS_Role_school",
            joinColumns = @JoinColumn(name = "roleId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "schoolId", referencedColumnName = "id")
    )
    private Set<ExpSchool> expSchools = new HashSet<>();

    public Set<ExpSchool> getExpSchools() {
        return expSchools;
    }

    public void setExpSchools(Set<ExpSchool> expSchools) {
        this.expSchools = expSchools;
    }


    public  Set<SYS_Menu> getMenuList() {
        return menuList;
    }

    public void setMenuList( Set<SYS_Menu> menuList) {
        this.menuList = menuList;
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

    public Set<ExpUser> getUserList() {
        return userList;
    }

    public void setUserList(Set<ExpUser> userList) {
        this.userList = userList;
    }
}
