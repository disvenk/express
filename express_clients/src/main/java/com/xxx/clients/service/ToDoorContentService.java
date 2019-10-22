package com.xxx.clients.service;

import com.xxx.core.service.CommonService;
import com.xxx.model.business.ExpDeliveryOrderNo;
import com.xxx.model.business.ExpSchoolWorkTime;
import com.xxx.model.business.ExpServiceFee;
import com.xxx.user.security.CurrentUser;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: disvenk.dai
 * @Date: 下午 3:43 2018/1/17 0017
 */
@Service
public class ToDoorContentService extends CommonService{

    /**
     * @Description:网点上门时间查询
     * @Author: disvenk.dai
     * @Date: 下午 3:40 2018/1/17 0017
     */
    public List<ExpSchoolWorkTime> findToDoorTimeBySchool(Integer type){
        List<ExpSchoolWorkTime> list = getCurrentSession().createCriteria(ExpSchoolWorkTime.class)
                .add(Restrictions.eq("schoolId", CurrentUser.get().schoolId))
                .add(Restrictions.eq("type", type))
                .add(Restrictions.eq("logicDeleted",false))
                .list();
        return list;
    }


}
