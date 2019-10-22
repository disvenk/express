package com.xxx.admin.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.MenuidsSaveForm;
import com.xxx.admin.form.RoleListForm;
import com.xxx.admin.form.RoleSaveForm;
import com.xxx.admin.service.RoleService;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.ExpSchool;
import com.xxx.model.system.SYS_Menu;
import com.xxx.model.system.SYS_Role;
import com.xxx.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("role")
public class RoleShowController {

    @Autowired
    private RoleService roleService;

    /**
     * @Description:获取角色列表信息
     * @Author: hanchao
     * @Date: 2017/12/27 0027
     */
    @RequestMapping(value = "/getRoleListAllByUser", method = {RequestMethod.POST})
    public ResponseEntity getRoleListAll(@RequestBody RoleListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        pageQuery.limit = 999;
        // JSONArray data = new JSONArray();
        //1.通过查询条件 获取一级菜单
        PageList<SYS_Role> list = roleService.getRoleListAll(pageQuery);
        JSONArray data = new JSONArray();
        for (SYS_Role role : list) { //所有一级菜单
            JSONObject json = new JSONObject();
            json.put("id", role.getId());
            json.put("name", role.getName() == null ? "" : role.getName());
            json.put("desc",role.getRemark()==null?"":role.getRemark());
            json.put("updateDate", DateTimeUtils.parseStr(role.getUpdateDate()));
            JSONArray roleList = new JSONArray();
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功",data, pageQuery.page, pageQuery.limit,list.total) , HttpStatus.OK);
    }

    /**
     * @Description:新增和编辑角色
     * @Author: disvenk.dai
     * @Date: 2018/1/2
     */
    @RequestMapping(value = "/newRoleAddAndEditorManage", method = {RequestMethod.POST})
    public ResponseEntity newMenuAddManage(@RequestBody RoleSaveForm form) throws Exception {
        if(form.name==null && StringUtils.isBlank(form.name)){
            return new ResponseEntity(new RestResponseEntity(110,"名称不能为空",null),HttpStatus.OK);
        }
        roleService.newRoleAddAndEditorManage(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null),HttpStatus.OK);
    }

    /**
     * @Description:角色编辑详情
     * @Author: disvenk.dai
     * @Date: 2018/1/2
     */
    @RequestMapping(value = "/roleCheckDetail", method = {RequestMethod.POST})
    public ResponseEntity roleEditDetail(@RequestBody MenuidsSaveForm form) throws Exception {
        JSONObject json = roleService.checkRole(form.id);

        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }


    /**
     * @Description:根据用户角色查找菜单
     * @Author: disvenk.dai
     * @Date: 2018/1/10
     */
    @RequestMapping(value = "/findMenuList", method = {RequestMethod.POST})
    @ResponseBody
    public JSONArray findMenuList() throws Exception {
        //JSONArray jsonArray = new JSONArray();
        //List<SYS_Menu> list = roleService.findMenuByUser();
        JSONArray jsonArray = roleService.findMenuByUser();
       /* List<SYS_Menu> allMenu = new ArrayList<>();
        for (SYS_Menu sys_menu: list ) {
            if(sys_menu.getChildren()!=null && sys_menu.getChildren().size()!=0){
                for (SYS_Menu sys_menu1: sys_menu.getChildren()) {
                    allMenu.add(sys_menu1);
                }
                allMenu.add(sys_menu);
            }
        }
        for (SYS_Menu sys_menu: allMenu) {
            JSONObject json = new JSONObject();
            json.put("id",sys_menu.getId());
            json.put("pId",sys_menu.getParentId()!=null ? sys_menu.getParentId().toString() : "0");
            json.put("name",sys_menu.getName());
            json.put("page",null);
            jsonArray.add(json);*/
        //}
        return jsonArray;
    }

    /**
     * @Description:删除角色
     * @Author: disvenk.dai
     * @Date: 2018/1/3
     */
    @RequestMapping(value = "/deleteRoleId",method = {RequestMethod.POST})
    public ResponseEntity deleteSecondRoleId(@RequestBody RoleSaveForm form) throws ResponseEntityException {
        if(form.id==null){
            return new ResponseEntity(new RestResponseEntity(110,"id不能为空",null), HttpStatus.OK);
        }
        roleService.deleteRole(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:根据用户获取授权网点
     * @Author: disvenk.dai
     * @Date: 2018/1/11
     */
    @RequestMapping(value = "/getSchoolListByUser", method = {RequestMethod.POST})
    public ResponseEntity getAllSchoolList() throws Exception {
        List<ExpSchool> expSchoolList = roleService.getAllSchoolListByUser();
        JSONArray data = new JSONArray();
        for (ExpSchool exp : expSchoolList) {
            JSONObject json = new JSONObject();
            json.put("id", exp.getId());
            json.put("name", exp.getName());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功", data), HttpStatus.OK);
    }
}
