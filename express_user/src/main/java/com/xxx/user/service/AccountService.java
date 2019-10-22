package com.xxx.user.service;

import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.ExpSchool;
import com.xxx.model.business.ExpUser;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.security.GenericLogin;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AccountService extends CommonService {
        @Autowired
        private UserSchoolService userSchoolService;

    /**
     * @Description: 刷新最后一次登录时间
     * @Author: Chen.zm
     * @Date: 2018/1/4 0004
     */
    public void updateFinalLoginDate(ExpUser userLogin){
        try {
            userLogin.setLastLoginDate(userLogin.getFinalLoginDate());
            userLogin.setFinalLoginDate(new Date());
            upsert2(userLogin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 获取用户信息
     * @Author: Chen.zm
     * @Date: 2018/1/4 0004
     */
    public ExpUser getExpUser(String userCode, String userMobile, Integer loginType) {
        Criterion cri = Restrictions.eq("loginType", loginType);
        cri = Restrictions.and(cri, Restrictions.or(Restrictions.eq("userCode", userCode), Restrictions.eq("userMobile", userMobile)));
        return (ExpUser) getCurrentSession().createCriteria(ExpUser.class)
                .add(cri).uniqueResult();
    }



    /**
     * 用户登录 账户密码登录
     * @param userCode
     * @param password
     * @return
     */
    public GenericLogin processLogin(String userCode, String password, Integer loginType) throws Exception {
        ExpUser userLogin = getExpUser(userCode, userCode, loginType);
        if (userLogin != null && !password.equals(userLogin.getUserPassword())) {
            throw new ResponseEntityException(200, "用户名或密码不正确");
        }
        if (userLogin != null && userLogin.isLogicDeleted()) {
            throw new ResponseEntityException(220, "账户已被禁用");
        }
        if (userLogin == null)
            throw new ResponseEntityException(200, "用户名或密码不正确");
        // AccountCache.putLoginData(genericLogin.userId, genericLogin.loginType, genericLogin);
        return returnGenericLogin(userLogin);
    }

    /**
     * 获取登录信息
     * @param userLogin
     * @return
     */
    public GenericLogin returnGenericLogin(ExpUser userLogin) throws ResponseEntityException {
        //刷新最后登录时间
        updateFinalLoginDate(userLogin);

        int userStatus = 0;
//        Integer workerType = 0;
        //名称为空会导致序列化异常
        String userName = userLogin.getUserNickname() == null ? " " : userLogin.getUserNickname();
        //处理用户未绑定网点的情况
        if (userLogin.getSchoolId() == null) {
            userStatus = 1;
        }
        List<ExpSchool> list = userSchoolService.getAllSchoolListByUser(userLogin.getId());
        List<Integer> ids = new ArrayList<>();
        for (ExpSchool expSchool: list) {
            ids.add(expSchool.getId());
        }
        GenericLogin genericLogin = new GenericLogin(userLogin.getId(), userLogin.getLoginType(), userName, userStatus, false,userLogin.getSchoolId(),ids);
        return genericLogin;
    }

    /**
     * @Description: 注册
     * @Author: Chen.zm
     */
    public GenericLogin saveRegisterUser(String userCode, String phoneNumber, String password, Integer loginType, String name) throws UpsertException, ResponseEntityException {
        if (userCode == null) userCode = phoneNumber;
        if (name == null) name = phoneNumber;

        ExpUser userLogin = getExpUser(userCode, phoneNumber, loginType);
        if (userLogin != null)
            throw new ResponseEntityException(200, "账户已存在");

        userLogin = new ExpUser();
        userLogin.setLoginType(loginType);
        userLogin.setUserCode(userCode);
        userLogin.setUserMobile(phoneNumber);
        userLogin.setUserPassword(password);
        userLogin.setUserNickname(name);
        userLogin = upsert2(userLogin);

        return returnGenericLogin(userLogin);
    }


    /**
     * @Description: 用户绑定网点
     * @Author: Chen.zm
     * @Date: 2018/1/4 0004
     */
    public void saveSchool(Integer userId, Integer schoolId) throws UpsertException, ResponseEntityException {
        ExpUser user = get2(ExpUser.class, userId);
        if (user == null)
            throw new ResponseEntityException(200, "账户不存在");
        if (user.getSchoolId() != null)
            throw new ResponseEntityException(220, "该账户已经绑定学校");
        ExpSchool expSchool = get2(ExpSchool.class, schoolId);
        if (expSchool == null)
            throw new ResponseEntityException(210, "学校不存在");
        user.setSchoolId(schoolId);
        upsert2(user);
    }


    /** 修改名称
     * @Description:
     * @Author: Chen.zm
     * @Date: 2017/11/8 0008
     */
    public void updateName(Integer userId, String userName) throws UpsertException, ResponseEntityException {
        ExpUser user = get2(ExpUser.class, userId);
        if (user == null)
            throw new ResponseEntityException(200, "账户不存在");
        user.setUserNickname(userName);
        upsert2(user);
    }

    /** 修改头像
     * @Description:
     * @Author: Chen.zm
     * @Date: 2017/11/8 0008
     */
    public void updateIcon(Integer userId, String icon) throws UpsertException, ResponseEntityException {
        ExpUser user = get2(ExpUser.class, userId);
        if (user == null)
            throw new ResponseEntityException(200, "账户不存在");
        user.setIcon(icon);
        upsert2(user);
    }

    /**
     * @Description:修改密码
     * @Author: hanchao
     * @Date: 2018/1/22 0022
     */
    public void updatePassword(Integer uerId,String password) throws ResponseEntityException, UpsertException {
        ExpUser user = get2(ExpUser.class, uerId);
        if (user == null)
            throw new ResponseEntityException(200, "账户不存在");
        user.setUserPassword(password);
        upsert2(user);
    }

}
