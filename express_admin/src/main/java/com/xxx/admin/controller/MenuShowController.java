package com.xxx.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.IdForm;
import com.xxx.admin.form.MenuListForm;
import com.xxx.admin.form.MenuidsSaveForm;
import com.xxx.admin.service.MenuShowService;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.ParseException;
import java.util.List;

@Controller
@RequestMapping("/menu")
public class MenuShowController {
    @Autowired
    private MenuShowService menuShowService;

    /**
     * @Description:平台菜单根据id删除二级菜单
     * @Author: disvenk.dai
     * @Date: 2017/12/27 0027
     */
    @RequestMapping(value = "/deleteMenuId", method = {RequestMethod.POST})
    public ResponseEntity deleteMenuId(@RequestBody IdForm form) throws ResponseEntityException, UpsertException {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "菜单id不能为空", null), HttpStatus.OK);
        menuShowService.deleteMenuId(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:平台菜单根据id删除一级菜单
     * @Author: disvenk.dai
     * @Date: 2017/12/27 0027
     */
    @RequestMapping(value = "/deleteMenuParentId", method = {RequestMethod.POST})
    public ResponseEntity deleteMenuParentId(@RequestBody IdForm form) throws ResponseEntityException, UpsertException {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "菜单id不能为空", null), HttpStatus.OK);
        menuShowService.deleteMenuParentId(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:新增一级菜单
     * @Author: hanchao
     * @Date: 2017/12/27 0027
     */
    @RequestMapping(value = "/newMenuAddManage", method = {RequestMethod.POST})
    public ResponseEntity newMenuAddManage(@RequestBody MenuidsSaveForm form) throws Exception {
        if(form.name==null && StringUtils.isBlank(form.name))
            return new ResponseEntity(new RestResponseEntity(110,"菜单名称不能为空",null), HttpStatus.OK);
        try {
            menuShowService.newMenuAddManage(form);
        } catch (ResponseEntityException e) {
            e.printStackTrace();
            return new ResponseEntity(new RestResponseEntity(100,"新增失败，请联系管理员",null), HttpStatus.OK);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:添加下级菜单
     * @Author: disvenk.dai
     * @Date: 2017/12/27 0027
     */
    @RequestMapping(value = "/menuAddManage", method = {RequestMethod.POST})
    public ResponseEntity menuAddManage(@RequestBody MenuidsSaveForm form) throws Exception {
        if (form.parentId == null)
            return new ResponseEntity(new RestResponseEntity(110, "父菜单id不能为空", null), HttpStatus.OK);
        menuShowService.menuAddManage(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:编辑父菜单
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @RequestMapping(value="/editMenu",method = {RequestMethod.POST})
    public ResponseEntity editorMenuParent(@RequestBody MenuidsSaveForm form) throws UpsertException, ResponseEntityException, ParseException {
        if(form.id==null)
            return new ResponseEntity(new RestResponseEntity(110,"id不能为空",null), HttpStatus.OK);
        if(form.name==null && StringUtils.isBlank(form.name))
            return new ResponseEntity(new RestResponseEntity(110,"菜单名称不能为空",null), HttpStatus.OK);
        Integer sort = menuShowService.getIndexP(form.id==null?form.parentId:form.id);
        if(form.sort>sort){
            return  menuShowService.updateMenuParentIdMax(form);
        }else if(form.sort<sort){
            return  menuShowService.updateMenuParentIdMin(form);
        }else{
            menuShowService.newMenuAddManage(form);
            return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
        }
    }

    /**
     * @Description:编辑子菜单
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @RequestMapping(value="/editMenuChild",method = {RequestMethod.POST})
    public ResponseEntity editorMenuChild(@RequestBody MenuidsSaveForm form) throws UpsertException, ResponseEntityException, ParseException {
        if(form.id==null)
            return new ResponseEntity(new RestResponseEntity(110,"id不能为空",null), HttpStatus.OK);
        if(form.parentId==null)
            return new ResponseEntity(new RestResponseEntity(110,"父id不能为空",null), HttpStatus.OK);
        if(form.name==null && StringUtils.isBlank(form.name))
            return new ResponseEntity(new RestResponseEntity(110,"菜单名称不能为空",null), HttpStatus.OK);
        Integer sort = menuShowService.getIndexC(form.id,form.parentId);
        if(form.sort>sort){
            return  menuShowService.updateMenuChildIdMax(form);
        }else if(form.sort<sort){
            return  menuShowService.updateMenuChildIdMin(form);
        }else{
            menuShowService.menuAddManage(form);
            return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
        }
    }

    /**
     * @Description:二级菜单菜单编辑保存
     * @Author: disvenk.dai
     * @Date: 2017/11/23 0023
     */
    @RequestMapping(value = "/updateMenuChild", method = {RequestMethod.POST})
    public ResponseEntity updateMenuChild(@RequestBody MenuidsSaveForm form) throws Exception {

        return menuShowService.updateMenuChild(form);
    }

    /**
     * @Description:菜单一级和二级详情
     * @Author: disvenk.dai
     * @Date: 2017/12/27 0027
     */
    @RequestMapping(value = "/menuEditDetail", method = {RequestMethod.POST})
    public ResponseEntity menuEditDetail(@RequestBody MenuidsSaveForm form) throws Exception {
        JSONObject json = menuShowService.getSYS_Menu(form.id,form.parentId);

        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }


    /**
     * @Description:获取菜单列表信息
     * @Author: disvenk.dai
     * @Date: 2017/12/27 0027
     */
    @RequestMapping(value = "/getMenuListAll", method = {RequestMethod.POST})
    public ResponseEntity getMenuListAll(@RequestBody MenuListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        pageQuery.limit = 999;
        // JSONArray data = new JSONArray();
        //1.通过查询条件 获取一级菜单
        List result = menuShowService.getMenuListAll(pageQuery);
        long total = (long) result.get(1);
        return new ResponseEntity(new PageResponseEntity(100, "成功",result.get(0), pageQuery.page, pageQuery.limit,total) , HttpStatus.OK);
    }

    /*
    * @Description:查询最大排序
    * @Author:disvenk.dai
    * @Date: 2017/12/27 0027
    * */
    @RequestMapping(value = "/checkMaxSort",method = RequestMethod.POST)
    public ResponseEntity checkMaxSort(){
        Integer maxSort = menuShowService.checkMaxSort();
        return new ResponseEntity(new RestResponseEntity(100,"成功",maxSort),HttpStatus.OK);
    }
}
