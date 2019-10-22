package com.xxx.admin.service;
import com.xxx.admin.form.ExpSchoolForm;
import com.xxx.admin.form.IdForm;
import com.xxx.admin.form.ReceiveTime;
import com.xxx.admin.form.SendTime;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.ExpSchool;
import com.xxx.model.business.ExpSchoolWorkTime;
import com.xxx.user.service.AccountService;
import com.xxx.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

@Service
public class SchoolService extends CommonService {

    /**
     * @Description:获取所有网点信息
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    public List<ExpSchool> getAllSchoolList() {
        Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
        return getCurrentSession().createCriteria(ExpSchool.class)
                .add(cri)
                .addOrder(Order.asc("id"))
                .list();
    }

    /**
     * @Description:获取维护时间信息
     * @Author: hanchao
     * @Date: 2018/1/12 0012
     */
    public List<ExpSchoolWorkTime> selectTimeById(Integer schoolId) {
        Criterion cri = Restrictions.and(Restrictions.eq("schoolId", schoolId));
        return getCurrentSession().createCriteria(ExpSchoolWorkTime.class)
                .add(cri)
                .list();
    }

    /**
     * @Description: 获取网点列表
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    public List<ExpSchool> findExpSchoolList(PageQuery pageQuery, String name,List<Integer> schoolIdList) {
        Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
        if( schoolIdList.size()!= 0 && schoolIdList!= null){
            cri = Restrictions.and(cri, Restrictions.in("id", schoolIdList));
        }
        if (StringUtils.isNotBlank(name)) {
             cri = Restrictions.and(cri,Restrictions.like("name", name, MatchMode.ANYWHERE));
            pageQuery.hibernateCriterion = cri;
        }
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        return hibernateReadonlyRepository.getList(ExpSchool.class, pageQuery);
    }

    /**
     * @Description:网点去重处理
     * @Author: hanchao
     * @Date: 2018/1/17 0017
     */
    public ExpSchool selectExpSchool(Integer schoolId,String name) {
        Criterion cri = Restrictions.and(Restrictions.ne("id", schoolId));
         cri = Restrictions.and(cri,Restrictions.eq("name", name));
         cri = Restrictions.and(cri,Restrictions.eq("logicDeleted", false));
        return (ExpSchool) getCurrentSession().createCriteria(ExpSchool.class)
                .add(cri)
                .uniqueResult();
    }

    /**
     * @Description:网点编辑保存
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    public ExpSchool updateExpSchool(ExpSchoolForm form) throws UpsertException,ResponseEntityException,ParseException {
        ExpSchool exp = get2(ExpSchool.class,form.id);
        if(exp == null)
            throw new ResponseEntityException(210, "网点信息不存在");
        exp.setName(form.name);
        exp.setTakedAddress(form.takedAddress);
        exp.setCapture(form.capture);
        exp.setIspaper(form.ispaper);
        String hql = "delete ExpSchoolWorkTime where schoolId = :schoolId";
        getCurrentSession().createQuery(hql).setInteger("schoolId", form.id).executeUpdate();
        if(form.sendTimeList != null && form.sendTimeList.size() >0){
            for(SendTime time : form.sendTimeList){
                ExpSchoolWorkTime expSchoolWorkTime = new ExpSchoolWorkTime();
                expSchoolWorkTime.setBeginDate(DateTimeUtils.parseDate(time.sendDoor1 +"","HH"));
                expSchoolWorkTime.setEndDate(DateTimeUtils.parseDate(time.sendDoor2 +"","HH"));
                expSchoolWorkTime.setSchoolId(form.id);
                expSchoolWorkTime.setType(time.type);
                upsert2(expSchoolWorkTime);
            }
        }
        if(form.receiveTimeList != null && form.receiveTimeList.size() >0){
            for(ReceiveTime time : form.receiveTimeList){
                ExpSchoolWorkTime expSchoolWorkTime = new ExpSchoolWorkTime();
                expSchoolWorkTime.setBeginDate(DateTimeUtils.parseDate(time.receiveDoor1 +"","HH"));
                expSchoolWorkTime.setEndDate(DateTimeUtils.parseDate(time.receiveDoor2 +"","HH"));
                expSchoolWorkTime.setSchoolId(form.id);
                expSchoolWorkTime.setType(time.type);
                upsert2(expSchoolWorkTime);
            }
        }
        return upsert2(exp);
    }

    /**
     * @Description:网点新增保存
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    public ExpSchool addExpSchool(ExpSchoolForm form) throws UpsertException,ResponseEntityException,ParseException {
        ExpSchool exp = new ExpSchool();
        exp.setName(form.name);
        exp.setTakedAddress(form.takedAddress);
        exp.setCapture(form.capture);
        exp.setIspaper(form.ispaper);
        exp = upsert2(exp);
        if(form.sendTimeList != null && form.sendTimeList.size() >0){
            for(SendTime time : form.sendTimeList){
                ExpSchoolWorkTime expSchoolWorkTime = new ExpSchoolWorkTime();
                expSchoolWorkTime.setBeginDate(DateTimeUtils.parseDate(time.sendDoor1 +"","HH"));
                expSchoolWorkTime.setEndDate(DateTimeUtils.parseDate(time.sendDoor2 +"","HH"));
                expSchoolWorkTime.setSchoolId(exp.getId());
                expSchoolWorkTime.setType(time.type);
                upsert2(expSchoolWorkTime);
            }
        }
        if(form.receiveTimeList != null && form.receiveTimeList.size() >0){
            for(ReceiveTime time : form.receiveTimeList){
                ExpSchoolWorkTime expSchoolWorkTime = new ExpSchoolWorkTime();
                expSchoolWorkTime.setBeginDate(DateTimeUtils.parseDate(time.receiveDoor1 +"","HH"));
                expSchoolWorkTime.setEndDate(DateTimeUtils.parseDate(time.receiveDoor2 +"","HH"));
                expSchoolWorkTime.setSchoolId(exp.getId());
                expSchoolWorkTime.setType(time.type);
                upsert2(expSchoolWorkTime);
            }
        }
        return exp;
    }

    /**
     * @Description:网点逻辑删除
     * @Author: hanchao
     * @Date: 2018/1/3 0003
     */
    public void schoolDelete(Integer id) throws ResponseEntityException, UpsertException {
        ExpSchool expSchool = get2(ExpSchool.class,id);
        if(expSchool == null)
            throw new ResponseEntityException(220, "网点信息不存在");
        expSchool.setLogicDeleted(true);
        upsert(expSchool);
    }

}
