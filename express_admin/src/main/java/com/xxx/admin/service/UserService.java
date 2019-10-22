package com.xxx.admin.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.UploadIconForm;
import com.xxx.admin.form.UserSaveForm;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.ExpSchool;
import com.xxx.model.business.ExpUser;
import com.xxx.model.system.SYS_Role;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.service.UploadFileService;
import com.xxx.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService extends CommonService {

    @Autowired
    private UploadFileService uploadFileService;

    /**
     * @Description:获取用户列表信息
     * @Author: disvenk.dai
     * @Date: 2018/1/3
     */
    @Cacheable(value = {"ExpUser"})
    public PageList<ExpUser> getUserListAll(PageQuery pageQuery) {
        Criterion cri = Restrictions.eq("logicDeleted", false);
        cri = Restrictions.and(cri,Restrictions.eq("loginType",3));
        cri = Restrictions.and(cri,Restrictions.eq("createdBy", CurrentUser.get().userId));
        pageQuery.hibernateCriterion = cri;
//        pageQuery.hibernateFetchFields = "plaProductCategory";
        PageList<ExpUser> list = hibernateReadonlyRepository.getList(ExpUser.class, pageQuery);
        ExpUser expUser = get2(ExpUser.class,"id",CurrentUser.get().userId);
        if(expUser.getUserCode().equals("admin")){
        list.add(expUser);
}

        return list;
    }

    /**
     * @Description:新增用户
     * @Author: disvenk.dai
     * @Date: 2018/1/6
     */
    @CacheEvict(value="ExpUser",allEntries = true)
    public ResponseEntity newUserddManage(UserSaveForm form) throws UpsertException {
        ExpUser expUser = null;
        if(form.id!=null){
            expUser = get2(ExpUser.class,"id",form.id);
            if(!expUser.getUserCode().equals(form.userName)){
                if(get2(expUser.getClass(),"userCode",form.userName)!=null)return new ResponseEntity(new RestResponseEntity(110, "用户名已存在",null) , HttpStatus.OK);
            }
            if(!expUser.getUserMobile().equals(form.tel)){
                if(get2(expUser.getClass(),"userMobile",form.tel)!=null)return new ResponseEntity(new RestResponseEntity(110, "手机号已被使用",null) , HttpStatus.OK);
            }
        }else {
            if(get2(ExpUser.class,"userCode",form.userName,"loginType",3)!=null)return new ResponseEntity(new RestResponseEntity(110, "用户名已存在",null) , HttpStatus.OK);
            if(get2(ExpUser.class,"userMobile",form.tel,"loginType",3)!=null)
                return  new ResponseEntity(new RestResponseEntity(110, "手机号已被使用",null) , HttpStatus.OK);

            expUser = new ExpUser();
        }
        expUser.setSchoolId(form.school);
        expUser.setRelName(form.relName);
        expUser.setLoginType(3);
        expUser.setUserMobile(form.tel);
        expUser.setUserCode(form.userName);
        if(form.id!=null && form.password!= null && StringUtils.isNotBlank(form.password)){
            expUser.setUserPassword(MD5Utils.md5Hex(form.password));
        }else if(form.id==null){
            expUser.setUserPassword(MD5Utils.md5Hex(form.password));
        }

        Set<SYS_Role> sys_roles = new HashSet<>();
        if(form.roleStr!=null && StringUtils.isNotBlank(form.roleStr)){
            String[] roleIds = form.roleStr.split(",");
            for (String roleId : roleIds) {
                SYS_Role sys_role = get2(SYS_Role.class,"id",Integer.parseInt(roleId));
                sys_roles.add(sys_role);
            }
        }

        expUser.setRoleList( sys_roles);
        ExpUser returnUser = upsert2(expUser);

        return  new ResponseEntity(new RestResponseEntity(100, "成功",null) , HttpStatus.OK);
    }

    /**
     * @Description:用户编辑详情
     * @Author: disvenk.dai
     * @Date: 2018/1/4
     */
    @Cacheable(value = {"ExpUser"})
    public JSONObject checkUser(Integer id) throws ResponseEntityException {
        ExpUser expUser = get2(ExpUser.class,"id", id);
        if (expUser == null) {
            throw new ResponseEntityException(210, "用户不存在");
        }
        JSONObject json = new JSONObject();
        json.put("id", expUser.getId());
        json.put("tel", expUser.getUserMobile() == null ? "" : expUser.getUserMobile());
        json.put("userName",expUser.getUserCode());
        json.put("relName",expUser.getRelName());
        JSONArray data = new JSONArray();
        Set<SYS_Role> roleList = expUser.getRoleList();
        if(roleList!=null && roleList.size()!=0){
            for (SYS_Role sys_role: roleList) {
                JSONObject json2 = new JSONObject();
                json2.put("id", sys_role.getId());
                json2.put("name",sys_role.getName());
                data.add(json2);
            }
            json.put("role",data);
        }



        return json;
    }

    /**
     * @Description:用户删除
     * @Author: disvenk.dai
     * @Date: 2018/1/8
     */
    @CacheEvict(value = "ExpUser",allEntries = true)
    public void deleteUser(UserSaveForm form){
        ExpUser expUser = get2(ExpUser.class,"id",form.id);
        expUser.setRoleList(null);
       Session session =getCurrentSession();
       if(expUser!=null)
       session.delete(expUser);

    }

    /**
     * @Description:密码重置
     * @Author: disvenk.dai
     * @Date: 2018/1/10
     */
    @CacheEvict(value = "ExpUser",allEntries = true)
    public void resetUserPass(UserSaveForm form) throws UpsertException {
        ExpUser expUser = get2(ExpUser.class,"id",form.id);
        expUser.setUserPassword(MD5Utils.md5Hex("888888"));
    upsert2(expUser);

    }

    /**
     * @Description:上传图片
     * @Author: disvenk.dai
     * @Date: 2018/1/3
     */
    public ExpUser uploadIcon(Integer id,UploadIconForm form) throws ResponseEntityException, UpsertException {
        ExpUser expUser = get2(ExpUser.class, "id", id);
        if (expUser == null) {
            throw new ResponseEntityException(220, "用户不存在");
        }
        if(expUser.getIcon()!=null){
        uploadFileService.deleteUploadFile(id);
        }
        expUser.setIcon(form.picture.get(0).href);
        return upsert2(expUser);
    }
}
