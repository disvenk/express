package com.xxx.admin.service;
import com.xxx.admin.form.ExpCustomerForm;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.ExpReceiveAddress;
import com.xxx.model.business.ExpSendAddress;
import com.xxx.model.business.ExpUser;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

@Service
public class PlatformService extends CommonService {

    /**
     * @Description: 获取客户列表
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    public List<ExpUser> findCustomerPageList(PageQuery pageQuery, String name,String userMobile,Integer schoolId) {
        Criterion cri = Restrictions.eq("loginType",3);
        if (StringUtils.isNotBlank(name)) {
             cri = Restrictions.like("name", name, MatchMode.ANYWHERE);
        }
        if (StringUtils.isNotBlank(userMobile)) {
             cri = Restrictions.like("userMobile", userMobile, MatchMode.ANYWHERE);
        }
        if(schoolId != null){
            cri = Restrictions.eq("schoolId", schoolId);
        }
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        return hibernateReadonlyRepository.getList(ExpUser.class, pageQuery);
    }

}
