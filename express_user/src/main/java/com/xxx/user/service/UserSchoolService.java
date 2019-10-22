package com.xxx.user.service;

import com.xxx.core.service.CommonService;
import com.xxx.model.business.ExpSchool;
import com.xxx.model.business.ExpUser;
import com.xxx.model.system.SYS_Role;
import com.xxx.user.security.CurrentUser;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserSchoolService extends CommonService {
    /**
     * @Description:根据用户查询授权网点
     * @Author: disvenk.dai
     * @Date: 2017/1/11
     */
    @Cacheable(value = {"ExpSchool"})
    public List<ExpSchool> getAllSchoolListByUser(Integer id){
        ExpUser expUser = get2(ExpUser.class, "id", id,"logicDeleted",false);
        Set<SYS_Role> roleList = expUser.getRoleList();
        List<ExpSchool> expSchools = new ArrayList<>();
        //根据用户查询角色
        for (SYS_Role sys_role : roleList ) {
            if(sys_role.getName().equals("admin")){
                List<ExpSchool> schools =  getAll(ExpSchool.class);
                return schools;
            }
            Set<ExpSchool> role_school= sys_role.getExpSchools();
            if(role_school!=null && role_school.size()!=0){
                for (ExpSchool expSchool: role_school ) {
                    expSchools.add(expSchool);
                }
            }
        }
        return  expSchools;
    }
}
