package com.xxx.admin.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.RoleSaveForm;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.ExpSchool;
import com.xxx.model.business.ExpUser;
import com.xxx.model.system.SYS_Menu;
import com.xxx.model.system.SYS_Role;
import com.xxx.user.security.CurrentUser;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleService extends CommonService {

    /**
     * @Description:获取角色列表信息
     * @Author: disvenk.dai
     * @Date: 2018/1/2
     */
    @Cacheable(value = {"SYS_Role"})
    public PageList<SYS_Role> getRoleListAll(PageQuery pageQuery) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        String hql = "select r from ExpUser u inner join u.roleList r where u.id = ?";
        List<SYS_Role> roleList =  getCurrentSession().createQuery(hql)
                .setInteger(0,CurrentUser.get().userId)
                .list();
        String roleName = null;
        for(SYS_Role sys_role : roleList){
            if(sys_role.getName().equals("admin")){
                roleName="admin";
            }
        }
        if(roleName!=null){
            return hibernateReadonlyRepository.getList(SYS_Role.class, pageQuery);
        }else {
            cri = Restrictions.eq("createdBy", CurrentUser.get().userId);
        }

        pageQuery.hibernateCriterion = cri;
//        pageQuery.hibernateFetchFields = "plaProductCategory";
        PageList<SYS_Role> list = hibernateReadonlyRepository.getList(SYS_Role.class, pageQuery);
      return list;
    }

    /**
     * @Description:新增和编辑角色
     * @Author: disvenk.dai
     * @Date: 2018/1/2
     */
    @CacheEvict(value="SYS_Role",allEntries=true)
    public SYS_Role newRoleAddAndEditorManage(RoleSaveForm form) throws UpsertException, ResponseEntityException {
        SYS_Role sys_role;
        if(form.id==null){
             sys_role = new SYS_Role();
            SYS_Role sys_role1 = get2(SYS_Role.class,"name",form.name);
              if(sys_role1!=null){
            throw new ResponseEntityException(220,"该角色名已存在");
        }
        }else {
             sys_role = get2(SYS_Role.class,"id",form.id);
             List<SYS_Role> list = getCurrentSession().createCriteria(SYS_Role.class)
                     .add(Restrictions.eq("logicDeleted",false))
                     .add(Restrictions.eq("name",form.name))
                    .list();
             if(form.name.equals(sys_role.getName()) && list.size()>1)
                 throw new ResponseEntityException(220,"该角色名已存在");
                 if(!form.name.equals(sys_role.getName()) && list.size()>=1)
                     throw new ResponseEntityException(220,"该角色名已存在");
        }

            sys_role.setName(form.name);
            sys_role.setRemark(form.desc);
            sys_role.setRemark(form.desc);
            if(form.menuIds!=null && StringUtils.isNotBlank(form.menuIds)) {
                String[] menuIds = form.menuIds.split(",");
                Set<SYS_Menu> list = new HashSet<>();
                for (String menuId : menuIds) {
                    SYS_Menu sys_menu = get2(SYS_Menu.class, "id", Integer.parseInt(menuId));
                    list.add(sys_menu);
                }
                sys_role.setMenuList(list);
            }else {
                sys_role.setMenuList(null);
            }

        if(form.schoolStr!=null && StringUtils.isNotBlank(form.schoolStr)) {
            String[] schoolIds = form.schoolStr.split(",");
            Set<ExpSchool> list = new HashSet<>();
            for (String schoolId : schoolIds) {
                ExpSchool expSchool = get2(ExpSchool.class, "id", Integer.parseInt(schoolId));
                list.add(expSchool);
            }
            sys_role.setExpSchools(list);
        }else {
            sys_role.setExpSchools(null);
        }

        return upsert2(sys_role);
    }


    /**
     * @Description:角色查看详情
     * @Author: disvenk.dai
     * @Date: 2018/1/3
     */
    @Cacheable(value = {"SYS_Role,ExpSchool"})
    public JSONObject checkRole(Integer id) throws ResponseEntityException {
        SYS_Role sys_role = get2(SYS_Role.class,"id", id);
        JSONObject json = new JSONObject();
        json.put("id", sys_role.getId());
        json.put("name", sys_role.getName() == null ? "" : sys_role.getName());
        json.put("desc",sys_role.getRemark()==null?"":sys_role.getRemark());
        JSONArray data1 = new JSONArray();
        JSONArray data2 = new JSONArray();
        Set<SYS_Menu> menuList = sys_role.getMenuList();
        Set<ExpSchool> schoolList = sys_role.getExpSchools();
        if(menuList!=null && menuList.size()!=0){
            for (SYS_Menu sys_menu: menuList) {
                            JSONObject json2 = new JSONObject();
                            json2.put("id", sys_menu.getId());
                            json2.put("pId",sys_menu.getParentId()!=null ? sys_menu.getParentId().toString() : "0");
                            json2.put("name",sys_menu.getName());
                            json2.put("page",null);
                            data1.add(json2);
                        }
                        json.put("menu",data1);
                    }
        if(schoolList!=null && schoolList.size()!=0){
            for (ExpSchool expSchool: schoolList) {
                JSONObject json2 = new JSONObject();
                json2.put("id", expSchool.getId());
                json2.put("name",expSchool.getName());
                data2.add(json2);
            }
            json.put("school",data2);
        }

        return json;
    }

    /**
     * @Description:根据用户查询授权菜单
     * @Author: disvenk.dai
     * @Date: 2017/1/6
     */
    @Cacheable(value = {"SYS_Menu"})
    public JSONArray findMenuByUser(){
        ExpUser expUser = get2(ExpUser.class, "id", CurrentUser.get().userId);
        Set<SYS_Role> roleList = expUser.getRoleList();
        List<SYS_Menu> sys_menus = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        JSONArray jsonArray1 = new JSONArray();
        //根据用户查询角色

        Boolean flag = false;
        for (SYS_Role sys_role : roleList) {
            if(sys_role.getName().equals("admin")){
                flag=true;
                break;
            }
        }

            if(flag){
         /*     List<SYS_Menu> sys_menus1 = getCurrentSession().createCriteria(SYS_Menu.class)
                      .add(Restrictions.isNull("parentId"))
                      .add(Restrictions.eq("logicDeleted",false))
                      .addOrder(Order.asc("sort"))
                      .list();*/
                List<SYS_Menu> sys_menus1 = getCurrentSession().createCriteria(SYS_Menu.class)
                        .add(Restrictions.eq("logicDeleted",false))
                        .addOrder(Order.asc("sort"))
                        .list();

                for (SYS_Menu sys_menu: sys_menus1 ) {
                    JSONObject json = new JSONObject();
                    json.put("id",sys_menu.getId());
                    json.put("pId",sys_menu.getParentId()!=null ? sys_menu.getParentId().toString() : "0");
                    json.put("name",sys_menu.getName());
                    json.put("page",null);
                    jsonArray1.add(json);
                }
              return jsonArray1;


            }
            //String hql = "select distinct m from ExpUser u inner join u.roleList r inner join r.menuList m where u.id = ? group by m.id order by m.sort asc";
            String hql = "select distinct m from ExpUser u left join u.roleList r left join r.menuList m where u.id = ? and m.parentId is null group by m.id order by m.sort asc";
            String hql1 = "select distinct m from ExpUser u left join u.roleList r left join r.menuList m where u.id = ? and m.parentId is not null group by m.id order by m.sort asc";

            List<SYS_Menu> menuList = getCurrentSession().createQuery(hql).setInteger(0, CurrentUser.get().userId).list();
            List<SYS_Menu> menuList2 = getCurrentSession().createQuery(hql1).setInteger(0, CurrentUser.get().userId).list();

            //final List<SYS_Menu> role_menu = getCurrentSession().createQuery(hql).setInteger(0,CurrentUser.get().userId).list();
            //Set<SYS_Menu> role_menu = sys_role.getMenuList();
            /*if(role_menu!=null && role_menu.size()!=0){
                for (SYS_Menu sys_menu: role_menu ) {
                    sys_menus.add(sys_menu);
                }
            }*/

            for (SYS_Menu sys_menu: menuList ) {
                JSONObject json = new JSONObject();
                json.put("id",sys_menu.getId());
                json.put("pId",sys_menu.getParentId()!=null ? sys_menu.getParentId().toString() : "0");
                json.put("name",sys_menu.getName());
                json.put("page",null);
                jsonArray.add(json);

            }

            for (SYS_Menu sys_menu: menuList2) {
                JSONObject json = new JSONObject();
                json.put("id",sys_menu.getId());
                json.put("pId",sys_menu.getParentId()!=null ? sys_menu.getParentId().toString() : "0");
                json.put("name",sys_menu.getName());
                json.put("page",null);
                jsonArray.add(json);
            }

        return  jsonArray;
    }

    /**
     * @Description:根据用户查询授权网点
     * @Author: disvenk.dai
     * @Date: 2017/1/11
     */
    @Cacheable(value = {"ExpSchool"})
    public List<ExpSchool> getAllSchoolListByUser(){
        ExpUser expUser = get2(ExpUser.class, "id", CurrentUser.get().userId,"logicDeleted",false);
        Set<SYS_Role> roleList = expUser.getRoleList();
        List<ExpSchool> expSchools = new ArrayList<>();
        //根据用户查询角色
        for (SYS_Role sys_role : roleList ) {
            if(sys_role.getName().equals("admin")){
                List<ExpSchool> schools =  getCurrentSession().createCriteria(ExpSchool.class)
                        .add(Restrictions.eq("logicDeleted",false))
                        .list();
                return schools;
            }
            Set<ExpSchool> role_school= sys_role.getExpSchools();
            if(role_school!=null && role_school.size()!=0){
                for (ExpSchool expSchool: role_school ) {
                    if(expSchool.isLogicDeleted()!=true){
                        expSchools.add(expSchool);
                    }

                }
            }
        }
        return  expSchools;
    }


    /**
     * @Description:删除角色
     * @Author: disvenk.dai
     * @Date: 2018/1/3
     */
    @CacheEvict(value = "SYS_Role",allEntries = true)
    public void deleteRole(Integer id) throws ResponseEntityException {
        SYS_Role sys_role = get2(SYS_Role.class,"id",id);
        if (sys_role == null) {
            throw new ResponseEntityException(210, "角色不存在");
        }
        sys_role.setMenuList(null);
        sys_role.setUserList(null);
    getCurrentSession().delete(sys_role);
    }

}
