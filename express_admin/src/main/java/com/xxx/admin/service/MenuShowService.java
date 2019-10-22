package com.xxx.admin.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.MenuidsSaveForm;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.core.service.CommonService;
import com.xxx.model.system.SYS_Menu;
import com.xxx.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class MenuShowService extends CommonService {

    /**
     * @Description:获取菜单列表信息
     * @Author: disvenk.dai
     * @Date: 2017/12/27 0027
     */
    @Cacheable(value = {"SYS_Menu"})
    public List getMenuListAll(PageQuery pageQuery) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        cri = Restrictions.and(cri, Restrictions.isNull("parentId"));
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "asc";
        pageQuery.sort = "sort";
        PageList<SYS_Menu> list = hibernateReadonlyRepository.getList(SYS_Menu.class, pageQuery);
        JSONArray data = new JSONArray();
        for (SYS_Menu menu : list) { //所有一级菜单
            JSONObject json = new JSONObject();
            json.put("id", menu.getId());
            json.put("name", menu.getName() == null ? "" : menu.getName());
            json.put("sort", menu.getSort() == null ? "" : menu.getSort());
            json.put("href", menu.getHref()== null ? "" : menu.getHref());
            json.put("target", menu.getTarget()== null ? "" : menu.getTarget());
            json.put("remark", menu.getRemark()== null ? "" : menu.getRemark());
            json.put("updateDate", DateTimeUtils.parseStr(menu.getUpdateDate()));
            JSONArray menuList = new JSONArray();
            //2.通过查询条件 根据一级菜单获取二级菜单
            List<SYS_Menu> list2 = getCurrentSession().createCriteria(SYS_Menu.class)
                    .add(Restrictions.eq("logicDeleted",false))
                    .add(Restrictions.eq("parentId",menu.getId()))
                    .addOrder(Order.asc("sort"))
                    .list();
            for (SYS_Menu cat : list2) {
                JSONObject js = new JSONObject();
                js.put("id", cat.getId());
                js.put("name", cat.getName() == null ? "" : cat.getName());
                js.put("sort", cat.getSort() == null ? "" : cat.getSort());
                js.put("href", cat.getHref()== null ? "" : cat.getHref());
                js.put("target", cat.getTarget()== null ? "" : cat.getTarget());
                js.put("remark", cat.getRemark()== null ? "" : cat.getRemark());
                js.put("updateDate", DateTimeUtils.parseStr(cat.getUpdateDate()));
                menuList.add(js);
            }
            json.put("menuList", menuList);
            data.add(json);
        }
        List result = new ArrayList();
        result.add(data);
        result.add(list.total);
        return result;
    }

    /**
     * @Description:新增一级菜单
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @CacheEvict(value = "SYS_Menu",allEntries = true)
    public SYS_Menu newMenuAddManage(MenuidsSaveForm form) throws UpsertException, ResponseEntityException {
        SYS_Menu sys_menu = null;
        if(form.id!=null || form.parentId!=null){
            SYS_Menu menu = get2(SYS_Menu.class,form.id==null?form.parentId:form.id);
            menu.setName(form.name);
            if(form.icon!=null && StringUtils.isNotBlank(form.icon)){
                menu.setIcon("&#"+form.icon+";");
            }
            menu.setRemark(form.remark);
            return upsert2(menu);
        }else {
            SYS_Menu menu = new SYS_Menu();
            String sql = "select count(*) from sys_menu where parent_id is null and logicDeleted=0";
            String countStr =  getCurrentSession().createSQLQuery(sql).uniqueResult().toString();
            Integer count = Integer.parseInt(countStr);
            String hql = "select max(s.sort) from sys_menu s where parent_id is null";
            Session session =getCurrentSession();
            Object o = session.createSQLQuery(hql).uniqueResult();
            String max = null;
            if(o!=null){
                max = o.toString();
            }else {
                max="0";
            }
            Double  maxSort = Double.parseDouble(max);
            List<SYS_Menu> menuList = session.createCriteria(SYS_Menu.class)
                    .add(Restrictions.isNull("parentId"))
                    .add(Restrictions.eq("logicDeleted",false))
                    .addOrder(Order.asc("sort"))
                    .list();
            if(form.sort != null){
                Integer sort = Integer.parseInt(form.sort.toString().substring(0,form.sort.toString().indexOf(".")));
                //如果设置的排序大于总数量，那么就在最大排序后添加一位
                if(form.sort>count){
                    menu.setName(form.name);
                    if(form.icon!=null && StringUtils.isNotBlank(form.icon)){
                        menu.setIcon("&#"+form.icon+";");
                    }else{
                        menu.setIcon("&#xe620;");
                    }
                    menu.setRemark(form.remark);
                    menu.setSort(maxSort+1);
                    menu.setUpdateDate(new Date());
                    return upsert2(menu);
                }else if(form.sort==1.0){
                    menu.setName(form.name);
                    if(form.icon!=null && StringUtils.isNotBlank(form.icon)){
                        menu.setIcon("&#"+form.icon+";");
                    }else{
                        menu.setIcon("&#xe620;");
                    }
                    menu.setRemark(form.remark);
                    menu.setSort(menuList.get(sort-1).getSort()-0.01);
                    menu.setUpdateDate(new Date());
                    return upsert2(menu);
                }
                menu.setName(form.name);
                if(form.icon!=null && StringUtils.isNotBlank(form.icon)){
                    menu.setIcon("&#"+form.icon+";");
                }else{
                    menu.setIcon("&#xe620;");
                }
                menu.setRemark(form.remark);
                menu.setSort(menuList.get(sort-1).getSort()+0.01);
                menu.setUpdateDate(new Date());
                return upsert2(menu);

            }else {
                menu.setName(form.name);
                if(form.icon!=null && StringUtils.isNotBlank(form.icon)){
                    menu.setIcon("&#"+form.icon+";");
                }else{
                    menu.setIcon("&#xe620;");
                }
                menu.setRemark(form.remark);
                menu.setSort(Double.parseDouble(maxSort.toString())+1);
                menu.setUpdateDate(new Date());
                return upsert2(menu);
            }
        }

    }

    /**
     * @Description:添加下级菜单
     * @Author: disvenk.dai
     * @Date: 2017/12/27 0027u.setName(f
     */
    @CacheEvict(value = {"SYS_Menu"}, allEntries = true)
    public SYS_Menu menuAddManage(MenuidsSaveForm form) throws UpsertException, ResponseEntityException, ParseException {
        SYS_Menu sys_menu = null;
        if(form.id!=null){
            SYS_Menu menu = get2(SYS_Menu.class,form.id);
            menu.setName(form.name);
            menu.setIcon("&#"+form.icon+";");
            menu.setHref(form.href);
            menu.setRemark(form.remark);
            return upsert2(menu);
        }else {
        SYS_Menu menu = new SYS_Menu();
        String sql = "select count(*) from sys_menu where parent_id=" + form.id + " and logicDeleted=0";
        String countStr = getCurrentSession().createSQLQuery(sql).uniqueResult().toString();
        Integer count = Integer.parseInt(countStr);
        String hql = "select max(s.sort) from sys_menu s where parent_id=" + form.id + " and logicDeleted=0";
        Session session = getCurrentSession();
        Object o = session.createSQLQuery(hql).uniqueResult();
        String max = null;
        if (o != null) {
            max = o.toString();
        } else {
            max = "0";
        }
        Double maxSort = Double.parseDouble(max);
        List<SYS_Menu> menuList = session.createCriteria(SYS_Menu.class)
                .add(Restrictions.eq("parentId", form.id))
                .add(Restrictions.eq("logicDeleted", false))
                .addOrder(Order.asc("sort"))
                .list();
        if (form.sort != null) {
            Integer sort = Integer.parseInt(form.sort.toString().substring(0, form.sort.toString().indexOf(".")));
            //如果设置的排序大于总数量，那么就在最大排序后添加一位
            if (form.sort >=count) {
                menu.setName(form.name);
                if(form.icon!=null && StringUtils.isNotBlank(form.icon)){
                    menu.setIcon("&#"+form.icon+";");
                }else{
                    menu.setIcon("&#xe621;");
                }
                menu.setRemark(form.remark);
                menu.setSort(maxSort + 1);
                menu.setHref(form.href);
                menu.setParentId(form.parentId);
                menu.setUpdateDate(new Date());
                return upsert2(menu);
            } else if (form.sort == 1.0) {
                menu.setName(form.name);
                if(form.icon!=null && StringUtils.isNotBlank(form.icon)){
                    menu.setIcon(form.icon);
                }else{
                    menu.setIcon("&#xe621;");
                }
                menu.setRemark(form.remark);
                menu.setHref(form.href);
                menu.setParentId(form.parentId);
                menu.setSort(menuList.get(sort - 1).getSort() - 0.01);
                menu.setUpdateDate(new Date());
                return upsert2(menu);
            }else {
                menu.setName(form.name);
                if(form.icon!=null && StringUtils.isNotBlank(form.icon)){
                    menu.setIcon("&#"+form.icon+";");
                }else{
                    menu.setIcon("&#xe621;");
                }
                menu.setRemark(form.remark);
                menu.setParentId(form.parentId);
                menu.setHref(form.href);
                menu.setSort(menuList.get(sort - 1).getSort() - 0.01);
                menu.setUpdateDate(new Date());
                return upsert2(menu);
            }


        } else {
            menu.setName(form.name);
            if(form.icon!=null && StringUtils.isNotBlank(form.icon)){
                menu.setIcon("&#"+form.icon+";");
            }else{
                menu.setIcon("&#xe621;");
            }
            menu.setRemark(form.remark);
            menu.setParentId(form.parentId);
            menu.setHref(form.href);
            menu.setSort(Double.parseDouble(maxSort.toString()) + 1);
            menu.setUpdateDate(new Date());
            return upsert2(menu);
        }

    }
    }

    /**
     * @Description:一级菜单编辑保存Max
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @CacheEvict(value = {"SYS_Menu"}, allEntries = true)
    public ResponseEntity updateMenuParentIdMax(MenuidsSaveForm form) throws UpsertException, ResponseEntityException, ParseException {
        SYS_Menu menu = get2(SYS_Menu.class,"id",form.id);
        String sql = "select count(*) from sys_menu where parent_id is null and logicDeleted=0";
        String countStr =  getCurrentSession().createSQLQuery(sql).uniqueResult().toString();
        Integer count = Integer.parseInt(countStr);
        String hql = "select max(s.sort) from sys_menu s where parent_id is null";
        Session session =getCurrentSession();
        Object o = session.createSQLQuery(hql).uniqueResult();
        String max = null;
        if(o!=null){
            max = o.toString();
        }else {
            max="0";
        }
        Double  maxSort = Double.parseDouble(max);
        List<SYS_Menu> menuList = session.createCriteria(SYS_Menu.class)
                .add(Restrictions.isNull("parentId"))
                .add(Restrictions.eq("logicDeleted",false))
                .addOrder(Order.asc("sort"))
                .list();
        //如果menu存在
        if (menu == null) {
            throw new ResponseEntityException(210, "菜单不存在");
        }
        //如果排序字段不为空

        //更改为最大排序
        if(form.sort>count){
            menu.setName(form.name);
            if(form.icon!=null && StringUtils.isNotBlank(form.icon)){
                menu.setIcon("&#"+form.icon+";");
            }
            menu.setRemark(form.remark);
            menu.setSort(Double.parseDouble(maxSort.toString())+1);
            upsert2(menu);
            return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
        }

        menu.setName(form.name);
        menu.setSort(menuList.get(Integer.parseInt(form.sort
                .toString()
                .substring(0,form.sort
                        .toString()
                        .indexOf(".")))-1).getSort()+0.001);
        upsert2(menu);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:一级菜单编辑保存Min
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @CacheEvict(value = {"SYS_Menu"}, allEntries = true)
    public ResponseEntity updateMenuParentIdMin(MenuidsSaveForm form) throws UpsertException, ResponseEntityException, ParseException {
        SYS_Menu menu = get2(SYS_Menu.class,"id",form.id);
        String sql = "select count(*) from sys_menu where parent_id is null and logicDeleted=0";
        String countStr =  getCurrentSession().createSQLQuery(sql).uniqueResult().toString();
        Integer count = Integer.parseInt(countStr);
        String hql = "select max(s.sort) from sys_menu s where parent_id is null";
        Session session =getCurrentSession();
        Object o = session.createSQLQuery(hql).uniqueResult();
        String max = null;
        if(o!=null){
            max = o.toString();
        }else {
            max="0";
        }
        Double  maxSort = Double.parseDouble(max);
        List<SYS_Menu> menuList = session.createCriteria(SYS_Menu.class)
                .add(Restrictions.isNull("parentId"))
                .add(Restrictions.eq("logicDeleted",false))
                .addOrder(Order.asc("sort"))
                .list();
        //如果menu存在
        if (menu == null) {
            throw new ResponseEntityException(210, "菜单不存在");
        }
        //如果排序字段不为空

        //更改为最大排序
        if(form.sort>count){
            menu.setName(form.name);
            if(form.icon!=null && StringUtils.isNotBlank(form.icon)){
                menu.setIcon("&#"+form.icon+";");
            }
            menu.setRemark(form.remark);
            menu.setSort(Double.parseDouble(maxSort.toString())+1);
            upsert2(menu);
            return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
        }
        menu.setName(form.name);
        if(form.icon!=null && StringUtils.isNotBlank(form.icon)){
            menu.setIcon("&#"+form.icon+";");
        }
        menu.setSort(menuList.get(Integer.parseInt(form.sort
                .toString()
                .substring(0,form.sort
                        .toString()
                        .indexOf(".")))-1).getSort()-0.001);
        upsert2(menu);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:二级菜单编辑保存Max
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @CacheEvict(value = {"SYS_Menu"}, allEntries = true)
    public ResponseEntity updateMenuChildIdMax(MenuidsSaveForm form) throws UpsertException, ResponseEntityException, ParseException {
        SYS_Menu menu = get2(SYS_Menu.class,"id",form.id);
        String sql = "select count(*) from sys_menu where parent_id="+form.parentId+" and logicDeleted=0";
        String countStr =  getCurrentSession().createSQLQuery(sql).uniqueResult().toString();
        Integer count = Integer.parseInt(countStr);
        String hql = "select max(s.sort) from sys_menu s where parent_id="+form.parentId;
        Session session =getCurrentSession();
        Object o = session.createSQLQuery(hql).uniqueResult();
        String max = null;
        if(o!=null){
            max = o.toString();
        }else {
            max="0";
        }
        Double  maxSort = Double.parseDouble(max);
        List<SYS_Menu> menuList = session.createCriteria(SYS_Menu.class)
                .add(Restrictions.eq("parentId",form.parentId))
                .add(Restrictions.eq("logicDeleted",false))
                .addOrder(Order.asc("sort"))
                .list();
        //如果menu存在
        if (menu == null) {
            throw new ResponseEntityException(210, "菜单不存在");
        }
        //如果排序字段不为空

        //更改为最大排序
        if(form.sort>count){
            menu.setName(form.name);
            if(form.icon!=null && StringUtils.isNotBlank(form.icon)){
                menu.setIcon("&#"+form.icon+";");
            }
            menu.setRemark(form.remark);
            menu.setSort(Double.parseDouble(maxSort.toString())+1);
            upsert2(menu);
            return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
        }

        menu.setName(form.name);
        if(form.icon!=null && StringUtils.isNotBlank(form.icon)){
            menu.setIcon("&#"+form.icon+";");
        }
        menu.setSort(menuList.get(Integer.parseInt(form.sort
                .toString()
                .substring(0,form.sort
                        .toString()
                        .indexOf(".")))-1).getSort()+0.001);
        upsert2(menu);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:二级菜单编辑保存Min
     * @Author: disvenk.dai
     * @Date: 2018/1/9
     */
    @CacheEvict(value = {"SYS_Menu"}, allEntries = true)
    public ResponseEntity updateMenuChildIdMin(MenuidsSaveForm form) throws UpsertException, ResponseEntityException, ParseException {
        SYS_Menu menu = get2(SYS_Menu.class,"id",form.id);
        String sql = "select count(*) from sys_menu where parent_id="+form.parentId+" and logicDeleted=0";
        String countStr =  getCurrentSession().createSQLQuery(sql).uniqueResult().toString();
        Integer count = Integer.parseInt(countStr);
        String hql = "select max(s.sort) from sys_menu s where parent_id="+form.parentId;
        Session session =getCurrentSession();
        Object o = session.createSQLQuery(hql).uniqueResult();
        String max = null;
        if(o!=null){
            max = o.toString();
        }else {
            max="0";
        }
        Double  maxSort = Double.parseDouble(max);
        List<SYS_Menu> menuList = session.createCriteria(SYS_Menu.class)
                .add(Restrictions.eq("parentId",form.parentId))
                .add(Restrictions.eq("logicDeleted",false))
                .addOrder(Order.asc("sort"))
                .list();
        //如果menu存在
        if (menu == null) {
            throw new ResponseEntityException(210, "菜单不存在");
        }
        //如果排序字段不为空

        //更改为最大排序
        if(form.sort>count){
            menu.setName(form.name);
            if(form.icon!=null && StringUtils.isNotBlank(form.icon)){
                menu.setIcon("&#"+form.icon+";");
            }
            menu.setRemark(form.remark);
            menu.setSort(Double.parseDouble(maxSort.toString())+1);
            upsert2(menu);
            return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
        }
        menu.setName(form.name);
        if(form.icon!=null && StringUtils.isNotBlank(form.icon)){
            menu.setIcon("&#"+form.icon+";");
        }
        menu.setSort(menuList.get(Integer.parseInt(form.sort
                .toString()
                .substring(0,form.sort
                        .toString()
                        .indexOf(".")))-1).getSort()-0.001);
        upsert2(menu);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:二级菜单菜单编辑保存
     * @Author: disvenk.dai
     * @Date: 2017/12/27 0027
     */
    @CacheEvict(value = {"SYS_Menu"}, allEntries = true)
    public ResponseEntity updateMenuChild(MenuidsSaveForm form) throws UpsertException, ResponseEntityException, ParseException {
        SYS_Menu sys_menu = get2(SYS_Menu.class,form.parentId);
        sys_menu.setName(form.name);
        if(form.icon!=null && StringUtils.isNotBlank(form.icon)){
            sys_menu.setIcon("&#"+form.icon+";");
        }
        sys_menu.setHref(form.href);
        upsert2(sys_menu);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:菜单一级和二级详情
     * @Author: disvenk.dai
     * @Date: 2017/12/27 0027
     */
    @Cacheable(value = {"SYS_Menu"})
    public JSONObject getSYS_Menu(Integer id,Integer parentId) throws ResponseEntityException {
        SYS_Menu menu = get2(SYS_Menu.class,"id", id != null ? id : parentId);
        List<SYS_Menu> meunList = getCurrentSession().createCriteria(SYS_Menu.class)
                .add(Restrictions.isNull("parentId"))
                .add(Restrictions.eq("logicDeleted",false))
                .addOrder(Order.asc("sort"))
                .list();

        List<SYS_Menu> menuList1 = getCurrentSession().createCriteria(SYS_Menu.class)
                    .add(Restrictions.eq("parentId",menu.getParentId()))
                    .add(Restrictions.eq("logicDeleted",false))
                    .addOrder(Order.asc("sort"))
                    .list();


        JSONObject json = new JSONObject();
        json.put("id", menu.getId());
        json.put("name", menu.getName() == null ? "" : menu.getName());

        json.put("href", menu.getHref()== null ? "" : menu.getHref());
        json.put("target", menu.getTarget()== null ? "" : menu.getTarget());
        json.put("remark", menu.getRemark()== null ? "" : menu.getRemark());
        json.put("updateDate", DateTimeUtils.parseStr(menu.getUpdateDate()));
        if(menu.getParentId()==null){
            for(int i=0;i<meunList.size();i++){
                Double listSort = meunList.get(i).getSort();
                if(menu.getSort()==listSort){
                    json.put("sort", menu.getSort() == null ? "" : i+1);
                    json.put("flag",0);
                }
            }
        }else {
            for(int i=0;i<menuList1.size();i++){
                Double listSort = menuList1.get(i).getSort();
                if(menu.getSort()==listSort){
                    json.put("sort", menu.getSort() == null ? "" : i+1);
                    json.put("flag",1);
                }
            }
        }
        return json;
    }

    /**
     * @Description:平台菜单根据id删除二级菜单
     * @Author: disvenk.dai
     * @Date: 2017/12/27 0027
     */
    @CacheEvict(value = {"SYS_Menu"}, allEntries = true)
    public void deleteMenuId(Integer id) throws ResponseEntityException, UpsertException {
        SYS_Menu pla = get2(SYS_Menu.class,"id",id);
        if (pla == null) {
            throw new ResponseEntityException(210, "菜单不存在");
        }
        if(pla.getParent() == null ){
            throw new ResponseEntityException(220, "不能直接删除一级菜单");
        }
        getCurrentSession().delete(pla);
    }

    /**
     * @Description:平台菜单根据id删除一级菜单
     * @Author: disvenk.dai
     * @Date: 2017/12/27 0027
     */
    @CacheEvict(value = {"SYS_Menu"}, allEntries = true)
    public void deleteMenuParentId(Integer id) throws ResponseEntityException, UpsertException {
        SYS_Menu menu = (SYS_Menu) getCurrentSession().createCriteria(SYS_Menu.class)
                .add(Restrictions.and(Restrictions.eq("id", id)))
                .setFetchMode("children", FetchMode.JOIN)
                .uniqueResult();
        if (menu == null)
            throw new ResponseEntityException(210, "菜单不存在");
        if (menu.getChildren() != null && menu.getChildren().size() != 0)
            throw new ResponseEntityException(220, "不能直接删除一级菜单");
        getCurrentSession().delete(menu);
    }

    /**
     * @Description:平台菜单根据id删除一级菜单
     * @Author: disvenk.dai
     * @Date: 2017/12/27 0027
     */
    public Integer checkMaxSort(){
        String sql = "select count(*) from sys_menu where parent_id is null and logicDeleted=0";
        Integer countStr = Integer.parseInt(getCurrentSession().createSQLQuery(sql).uniqueResult().toString())+1;
        return countStr;
    }

    /**
     * @Description:查询父排序工具
     * @Author: disvenk.dai
     * @Date: 2018/1/10
     */
    public Integer getIndexP(Integer id) throws ResponseEntityException {
        List<SYS_Menu> list = getCurrentSession().createCriteria(SYS_Menu.class)
                .add(Restrictions.isNull("parentId"))
                .add(Restrictions.eq("logicDeleted", false))
                .addOrder(Order.asc("sort"))
                .list();

        SYS_Menu sys_menu = get2(SYS_Menu.class,id);
        for (int i=0;i<list.size();i++) {
            if(sys_menu.getId()==list.get(i).getId()){
                return i+1;
            }
        }
        return null;
    }

    /**
     * @Description:查询子排序工具
     * @Author: disvenk.dai
     * @Date: 2018/1/10
     */
    public Integer getIndexC(Integer id,Integer parentId) throws ResponseEntityException {
        List<SYS_Menu> list = getCurrentSession().createCriteria(SYS_Menu.class)
                .add(Restrictions.eq("parentId",parentId))
                .add(Restrictions.eq("logicDeleted", false))
                .addOrder(Order.asc("sort"))
                .list();

        SYS_Menu sys_menu = get2(SYS_Menu.class,id);
        for (int i=0;i<list.size();i++) {
            if(sys_menu.getId()==list.get(i).getId()){
                return i+1;
            }
        }
        return null;
    }
}
