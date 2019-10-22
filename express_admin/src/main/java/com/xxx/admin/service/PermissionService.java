package com.xxx.admin.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.ExpUser;
import com.xxx.model.system.SYS_Menu;
import com.xxx.model.system.SYS_Role;
import com.xxx.user.security.CurrentUser;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PermissionService extends CommonService {

    /**
     * @Description:根据用户查询边栏菜单
     * @Author: disvenk.dai
     * @Date: 2017/1/6
     */
    @Cacheable(value = {"SYS_Menu"})
    public JSONArray findMenuByUser(Integer userId) {
        ExpUser expUser = get2(ExpUser.class, "id", userId);
        Set<SYS_Role> roleList = expUser.getRoleList();
        JSONArray data1 = null;
        //根据用户查询角色
        Boolean flag = false;
        for (SYS_Role sys_role : roleList) {
            if(sys_role.getName().equals("admin")){
                flag=true;
                break;
            }
        }
            //如果是超级管理员
            if (flag) {
                //新建数组并查询所有的菜单
                JSONArray admin = new JSONArray();
                List<SYS_Menu> allMenu = getCurrentSession().createCriteria(SYS_Menu.class)
                        .add(Restrictions.eq("logicDeleted", false))
                        .add(Restrictions.isNull("parentId"))
                        .addOrder(Order.asc("sort"))
                        .list();

                for (SYS_Menu sys_menu : allMenu) {
                    if (sys_menu.getParentId() == null) {
                        List<SYS_Menu> list = getCurrentSession().createCriteria(SYS_Menu.class)
                                .add(Restrictions.eq("logicDeleted", false))
                                .add(Restrictions.eq("parentId", sys_menu.getId()))
                                .addOrder(Order.asc("sort"))
                                .list();
                        // Set<SYS_Menu> list = sys_menu.getChildren();
                        JSONObject json1 = new JSONObject();
                        json1.put("text", sys_menu.getName());
                        json1.put("icon", sys_menu.getIcon());
                        json1.put("href", sys_menu.getHref());
                        JSONArray data2 = new JSONArray();
                        if (list != null && list.size() != 0) {
                            for (SYS_Menu sys_menu1 : list) {
                                // if(sys_menu1.getParentId()==sys_menu.getId()){
                                JSONObject json2 = new JSONObject();
                                json2.put("text", sys_menu1.getName());
                                json2.put("icon", sys_menu1.getIcon());
                                json2.put("href", sys_menu1.getHref());
                                data2.add(json2);
                                // }
                            }
                        }
                        json1.put("subset", data2);
                        admin.add(json1);
                    }

                }

                return admin;
            } else {
                 data1 = new JSONArray();
                //String hql = "select distinct m from SYS_Menu m inner join fetch m.roleList r inner join fetch r.userList u where u.id = ? group by m.id order by m.sort asc";
                String hql = "select distinct m from ExpUser u left join u.roleList r left join r.menuList m where u.id = ? and m.parentId is null group by m.id order by m.sort asc";
                String hql1 = "select distinct m from ExpUser u left join u.roleList r left join r.menuList m where u.id = ? and m.parentId is not null group by m.id order by m.sort asc";
                //from Menu m inner join fetch m.roles r inner join fetch r.users u where u.id = ? order by m.priority
                 List<SYS_Menu> menuList = getCurrentSession().createQuery(hql).setInteger(0, userId).list();
                 List<SYS_Menu> menuList2 = getCurrentSession().createQuery(hql1).setInteger(0, userId).list();

                if (menuList != null && menuList.size() != 0) {
                   disvenk:for (SYS_Menu sys_menu : menuList) {
                        if(sys_menu==null)continue disvenk;
                        JSONObject json1 = new JSONObject();
                        //Set<SYS_Menu> list = sys_menu.getChildren();
                       /* List<SYS_Menu> list = getCurrentSession().createCriteria(SYS_Menu.class)
                                .add(Restrictions.eq("logicDeleted", false))
                                .add(Restrictions.eq("parentId", sys_menu.getId()))
                                .addOrder(Order.asc("sort"))
                                .list();*/
                        //if (sys_menu.getParentId() == null) {
                            json1.put("text", sys_menu.getName());
                            json1.put("icon", sys_menu.getIcon());
                            json1.put("href", sys_menu.getHref());
                            JSONArray data2 = new JSONArray();
                            if (menuList2 != null && menuList2.size() != 0) {
                                for (SYS_Menu sys_menu1 : menuList2) {
                                    if(sys_menu1.getParentId().equals(sys_menu.getId())){
                                        JSONObject json2 = new JSONObject();
                                        json2.put("text", sys_menu1.getName());
                                        json2.put("icon", sys_menu1.getIcon());
                                        json2.put("href", sys_menu1.getHref());
                                        data2.add(json2);
                                    }

                                }
                            }
                            json1.put("subset", data2);
                            data1.add(json1);
                       // }
                    }
                }


            }

return data1;
    }
}




