package com.xxx.admin.service;
import com.alibaba.fastjson.JSONArray;
import com.xxx.admin.form.ExpCustomerForm;
import com.xxx.admin.form.ExpSchoolForm;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.ExtFilter;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.ExpReceiveAddress;
import com.xxx.model.business.ExpReceiveAddress;
import com.xxx.model.business.ExpSchool;
import com.xxx.model.business.ExpSendAddress;
import com.xxx.model.business.ExpUser;
import com.xxx.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService extends CommonService {

    /**
     * @Description: 获取客户列表
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    public List<ExpUser> findCustomerPageList(PageQuery pageQuery, String name,String userMobile,Boolean isDelete,Integer schoolId,List<Integer> schoolIdList) {
        Criterion cri = Restrictions.eq("loginType",1);
        if(schoolIdList.size() > 0){
            cri = Restrictions.and(cri, Restrictions.in("schoolId", schoolIdList));
        }
        if (StringUtils.isNotBlank(name)) {
            cri = Restrictions.and(cri, Restrictions.like("userCode", name,MatchMode.ANYWHERE));
        }
        if (StringUtils.isNotBlank(userMobile)) {
            cri = Restrictions.and(cri, Restrictions.like("userMobile", userMobile,MatchMode.ANYWHERE));
        }
        if(schoolId != null){
            cri = Restrictions.and(cri, Restrictions.like("schoolId", schoolId));
        }
        if(isDelete != null){
            pageQuery.logicDeleted= isDelete;
        }
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        return hibernateReadonlyRepository.getList(ExpUser.class, pageQuery);
    }

    /**
     * @Description:判断用户名是否重复
     * @Author: hanchao
     * @Date: 2018/1/11 0011
     */
    public ExpUser selectIsRepetitionUserCode(Integer id,String userCode) throws UpsertException,ResponseEntityException {
        Criterion cri = Restrictions.ne("id", id);
        cri = Restrictions.and(cri,Restrictions.eq("loginType",1));
        cri = Restrictions.and(cri,Restrictions.eq("userCode",userCode));
        return (ExpUser) getCurrentSession().createCriteria(ExpUser.class)
                .add(cri)
                .uniqueResult();
    }

    /**
     * @Description:判断手机号是否重复
     * @Author: hanchao
     * @Date: 2018/1/11 0011
     */
    public ExpUser selectIsRepetitionUserMobile(Integer id,String userMobile) throws UpsertException,ResponseEntityException {
        Criterion cri = Restrictions.ne("id", id);
        cri = Restrictions.and(cri,Restrictions.eq("loginType",1));
        cri = Restrictions.and(cri,Restrictions.eq("userMobile",userMobile));
        return (ExpUser) getCurrentSession().createCriteria(ExpUser.class)
                .add(cri)
                .uniqueResult();
    }

    /**
     * @Description:客户编辑保存
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    public ExpUser updateCustomer(ExpCustomerForm form) throws UpsertException,ResponseEntityException,ParseException {
        ExpUser exp = get2(ExpUser.class,form.id);
        if(exp == null)
            throw new ResponseEntityException(210, "客户信息不存在");
        exp.setUserCode(form.userCode);
        exp.setUserMobile(form.userMobile);
        exp.setSchoolId(form.schoolId);
        exp.setRemarks(form.remark);
        exp.setUserNickname(form.userNickname);
        return upsert2(exp);
    }

    /**
     * @Description:客户信息逻辑删除
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    public void customerDelete(Integer id) throws ResponseEntityException, UpsertException {
        ExpUser exp = get2(ExpUser.class,id);
        if(exp == null)
            throw new ResponseEntityException(220, "客户信息不存在");
        exp.setLogicDeleted(true);
        upsert2(exp);
    }

    /**
     * @Description:客户寄件地址
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    public List<ExpSendAddress> getExpSendAddressList(PageQuery pageQuery,Integer id) throws UpsertException,ResponseEntityException,ParseException {
        Criterion cri = Restrictions.eq("userId", id);
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        return hibernateReadonlyRepository.getList(ExpSendAddress.class, pageQuery);
    }

    /**
     * @Description:修改客户寄件地址为默认
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    public void expSendAddressDefault(Integer id,Integer addressId) throws ResponseEntityException, UpsertException {
        Criterion cri = Restrictions.eq("userId", id);
        List<ExpSendAddress> list = getCurrentSession().createCriteria(ExpSendAddress.class).add(cri).list();
        for(ExpSendAddress expSendAddress : list){
            expSendAddress.setDefault(false);
            upsert2(expSendAddress);
        }
        ExpSendAddress exp = get2(ExpSendAddress.class,addressId);
        if(exp == null)
            throw new ResponseEntityException(220, "地址信息不存在");
        exp.setDefault(true);
        upsert2(exp);
    }
    /**
     * @Description:客户收件地址
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    public List<ExpReceiveAddress> getExpReceiveAddressList(PageQuery pageQuery,Integer id) throws UpsertException,ResponseEntityException,ParseException {
        Criterion cri = Restrictions.eq("userId", id);
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        return hibernateReadonlyRepository.getList(ExpReceiveAddress.class, pageQuery);
    }

    /**
     * @Description:修改客户收件地址为默认
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    public void ExpReceiveAddressDefault(Integer id,Integer addressId) throws ResponseEntityException, UpsertException {
        Criterion cri = Restrictions.eq("userId", id);
        List<ExpReceiveAddress> list = getCurrentSession().createCriteria(ExpReceiveAddress.class).add(cri).list();
        for(ExpReceiveAddress ExpReceiveAddress : list){
            ExpReceiveAddress.setDefault(false);
            upsert2(ExpReceiveAddress);
        }
        ExpReceiveAddress exp = get2(ExpReceiveAddress.class,addressId);
        if(exp == null)
            throw new ResponseEntityException(220, "地址信息不存在");
        exp.setDefault(true);
        upsert2(exp);
    }

}
