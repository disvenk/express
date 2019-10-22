package com.xxx.admin.service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.dao.SuplierDao;
import com.xxx.admin.form.*;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.ExtFilter;
import com.xxx.core.query.MybatisPageQuery;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.*;
import com.xxx.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

@Service
public class SendManageService extends CommonService {

    @Autowired
    private SuplierDao suplierDao;

    /**
     * @Description:根据网点权限显示快递公司信息
     * @Author: hanchao
     * @Date: 2018/1/22 0022
     */
    public List<ExpSchoolDeliveryCompany> findList(List<Integer> list) {
            Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
            if(list.size()!= 0 && list != null){
                cri = Restrictions.and(cri,Restrictions.in("schoolId", list));
            }
            return getCurrentSession().createCriteria(ExpSchoolDeliveryCompany.class)
                    .add(cri)
                    .addOrder(Order.desc("id"))
                    .list();
    }

    /**
     * @Description:对账管理
     * @Author: hanchao
     * @Date: 2018/1/12 0012
     */
    public PageList<JSONObject> findAccountManagePageList(MybatisPageQuery pageQuery) {
        return suplierDao.findList(pageQuery);
    }

    /**
     * @Description:寄件订单删除
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    public void expSendOrderDelete(Integer id) throws ResponseEntityException, UpsertException {
        ExpSendOrder exp = get2(ExpSendOrder.class,id);
        if(exp == null)
            throw new ResponseEntityException(210, "订单信息不存在");
        exp.setLogicDeleted(true);
        upsert2(exp);
    }

    /**
     * @Description:寄件订单编辑保存
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    public ExpSendOrder updateExpSendOrder(ExpSendOrderForm form) throws UpsertException,ResponseEntityException {
        ExpSendOrder exp = get2(ExpSendOrder.class,form.id);
       if(exp == null){
           throw new ResponseEntityException(210, "订单信息不存在");
       }
        if(StringUtils.isNotBlank(form.deliveryNo)){
            Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
            cri = Restrictions.and(cri, Restrictions.eq("deliveryNo", form.deliveryNo));
            cri = Restrictions.and(cri, Restrictions.ne("id",form.id));
            ExpSendOrder exp1 = (ExpSendOrder) getCurrentSession().createCriteria(ExpSendOrder.class)
                    .add(cri)
                    .uniqueResult();
            if(exp1 != null)
                throw new ResponseEntityException(220, "该订单的快递单号已被其他订单使用");

            ExpSchool expSchool = get2(ExpSchool.class,"id",exp.getSchoolId(),"logicDeleted",false);
            if(expSchool.getIspaper() == 0){
                Criterion cri1 = Restrictions.and(Restrictions.eq("logicDeleted", false));
                cri1 = Restrictions.and(cri1,Restrictions.eq("deliveryNo", form.deliveryNo));
                cri1 = Restrictions.and(cri1,Restrictions.eq("deliveryId", exp.getDeliveryId()));
                cri1 = Restrictions.and(cri1,Restrictions.eq("schoolId",exp.getSchoolId()));
                ExpDeliveryOrderNo expDeliveryOrderNo = (ExpDeliveryOrderNo) getCurrentSession().createCriteria(ExpDeliveryOrderNo.class)
                        .add(cri1)
                        .uniqueResult();
                if(expDeliveryOrderNo == null){
                    throw new ResponseEntityException(230, "请使用系统维护的快递单号");
                }
                expDeliveryOrderNo.setUsed(true);
                upsert2(expDeliveryOrderNo);
            }
        }
        exp.setDeliveryNo(form.deliveryNo);
        exp.setSendName(form.sendName);
        exp.setSendTel(form.sendTel);
        exp.setSendAddress(form.sendAddress);
        exp.setReceiverName(form.receiverName);
        exp.setReceiverTel(form.receiverTel);
        exp.setReceiverAddress(form.receiverAddress);
        exp.setDelivererId(form.delivererId);
        exp.setReceiverProvince(form.province);
        exp.setReceiverCity(form.city);
        exp.setReceiverZone(form.zone);
        exp.setSendProvince(form.sendProvince);
        exp.setSendCity(form.sendCity);
        exp.setSendZone(form.sendZone);
        return upsert2(exp);
    }

    /**
     * @Description:获取寄件订单的费用(上门取件)
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    public List<JSONObject> findSendOrderCount(JSONObject pageQuery) throws ResponseEntityException, UpsertException {
        return suplierDao.findSendOrderCount(pageQuery);
    }
    /**
     * @Description:获取寄件订单的费用(自行取件)
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    public List<JSONObject> findSendQtyCount(JSONObject pageQuery) throws ResponseEntityException, UpsertException {
        return suplierDao.findSendQtyCount(pageQuery);
    }

    /**
     * @Description:寄件订单列表
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    public List<ExpSendOrder> findSendOrderList(PageQuery pageQuery,Boolean isDelete,String userCode,String userMobile,Integer type,Integer orderStatus,
                                                String orderStartTime,String orderEndTime,Integer schoolId,List<Integer> schoolIdList, String orderNo, String deliveryNo) throws ParseException {
        Criterion cri = Restrictions.eq("appId",1);
        JSONArray jsonArray = new JSONArray();
        if(schoolIdList.size() > 0){
            cri = Restrictions.and(cri, Restrictions.in("schoolId", schoolIdList));
        }
        if (StringUtils.isNotBlank(userCode)) {
            ExtFilter filter = new ExtFilter("expUser.userCode", "string", userCode, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(userMobile)) {
            ExtFilter filter = new ExtFilter("expUser.userMobile", "string", userMobile, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
        }
        if(type != null){
            cri = Restrictions.and(cri,Restrictions.eq("type", type));
        }
        if(orderStatus != null){
            cri = Restrictions.and(cri,Restrictions.eq("orderStatus", orderStatus));
        }
        if(schoolId != null){
            cri = Restrictions.and(cri,Restrictions.eq("schoolId", schoolId));
        }
        if (StringUtils.isNotBlank(orderNo)) {
            cri = Restrictions.and(cri,Restrictions.like("orderNo", orderNo, MatchMode.ANYWHERE));
        }
        if (StringUtils.isNotBlank(deliveryNo)) {
            cri = Restrictions.and(cri,Restrictions.like("deliveryNo", deliveryNo, MatchMode.ANYWHERE));
        }
        if (StringUtils.isNotBlank(orderStartTime)) {
            cri = Restrictions.and(cri,Restrictions.ge("createdDate", DateTimeUtils.parseDate(orderStartTime,"yyyy-MM-dd")));
        }
        if (StringUtils.isNotBlank(orderEndTime)) {
            cri = Restrictions.and(cri,Restrictions.le("createdDate", DateTimeUtils.parseDate(orderEndTime,"yyyy-MM-dd")));
        }
        if(isDelete != null){
            pageQuery.logicDeleted = isDelete;
        }
        pageQuery.filter = jsonArray.toJSONString();
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        return hibernateReadonlyRepository.getList(ExpSendOrder.class, pageQuery);
    }

    /**
     * @Description:快递单批量删除
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    public void deliveryOrdernoDeleteIds(List<IdForm> ids) throws ResponseEntityException, UpsertException {
        for(IdForm id :ids){
            ExpDeliveryOrderNo exp = get2(ExpDeliveryOrderNo.class,id.id);
            if(exp == null)
                throw new ResponseEntityException(210, "快递单信息不存在");
            exp.setLogicDeleted(true);
            upsert2(exp);
        }
    }

    /**
     * @Description:快递单信息删除
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    public void deliveryOrdernoDelete(Integer id) throws ResponseEntityException, UpsertException {
        ExpDeliveryOrderNo exp = get2(ExpDeliveryOrderNo.class,id);
        if(exp == null)
            throw new ResponseEntityException(210, "快递单信息不存在");
        exp.setLogicDeleted(true);
        upsert2(exp);
    }

    /**
     * @Description:快递单新增
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    public void addDeliveryOrderno(DeliveryOrdernoForm form) throws UpsertException,ResponseEntityException {
        if(form.deliveryNoStart == null || form.deliveryNoEnd == null){
            throw new ResponseEntityException(210, "您添加的快递单号数据有误");
        }
        if(form.deliveryNoEnd > 500){
            throw new ResponseEntityException(220, "一次最多可以添加500张单号");
        }
        for(int a = form.deliveryNoStart; a < form.deliveryNoEnd+form.deliveryNoStart;a++){
            ExpDeliveryOrderNo expDelivery = get2(ExpDeliveryOrderNo.class,"deliveryNo",a+"","logicDeleted",false);
            if(expDelivery != null)
                throw new ResponseEntityException(230, "您添加的快递单号在系统已存在");
            ExpDeliveryOrderNo exp = new ExpDeliveryOrderNo();
            exp.setDeliveryNo(a+"");
            exp.setSchoolId(form.schoolId);
            exp.setUsed(false);
            exp.setDeliveryId(form.deliveryId);
            upsert2(exp);
        }
    }

    /**
     * @Description:快递单维护列表
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    public List<ExpDeliveryOrderNo> findDeliveryOrderNoPageList(PageQuery pageQuery,String deliveryNo,Integer deliveryId,Boolean isUsed,Integer schoolId,List<Integer> schoolIdList) {
        Criterion cri = Restrictions.eq("logicDeleted",false);
        if(schoolIdList.size() > 0){
            cri = Restrictions.and(cri, Restrictions.in("schoolId", schoolIdList));
        }
        if(StringUtils.isNotBlank(deliveryNo)){
            cri = Restrictions.and(cri,Restrictions.like("deliveryNo", deliveryNo,MatchMode.ANYWHERE));
        }
        if(deliveryId != null){
            cri = Restrictions.and(cri,Restrictions.eq("deliveryId", deliveryId));
        }
        if(isUsed != null){
            cri = Restrictions.and(cri,Restrictions.eq("isUsed", isUsed));
        }
        if(schoolId != null){
            cri = Restrictions.and(cri,Restrictions.eq("schoolId", schoolId));
        }
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        return hibernateReadonlyRepository.getList(ExpDeliveryOrderNo.class, pageQuery);
    }

    /**
     * @Description:快递费信息删除
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    public void deliveryProvinceServiceFeeDelete(Integer id) throws ResponseEntityException, UpsertException {
        ExpSchoolDeliveryCompany exp = get2(ExpSchoolDeliveryCompany.class,id);
        if(exp == null)
            throw new ResponseEntityException(210, "快递费信息不存在");
        exp.setLogicDeleted(true);
        upsert2(exp);
    }

    /**
     * @Description:快递费维护新增
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    public ExpSchoolDeliveryCompany addProvinceServiceFee(ExpDeliveryCompanyListForm form) throws UpsertException,ResponseEntityException {
        ExpSchoolDeliveryCompany exp = get2(ExpSchoolDeliveryCompany.class,"deliveryId",form.deliveryId,"schoolId",form.schoolId,"logicDeleted",false);
            if(exp != null){
                if(form.id == null) {
                    throw new ResponseEntityException(220, "当前省份不存在");
                }
            ExpProvinceServiceFee fee = get2(ExpProvinceServiceFee.class,"provinceId",form.id,"schoolDeliveryCompanyId",exp.getId(),"logicDeleted",false);
            if(fee == null){
                ExpProvinceServiceFee fees = new ExpProvinceServiceFee();
                fees.setSchoolDeliveryCompanyId(exp.getId());
                fees.setProvinceId(form.id);
                fees.setProvinceName(form.provinceName);
                if("firstWeightFee".equals(form.field))fees.setFirstWeightFee(form.value);
                if("otherWeightFee".equals(form.field))fees.setOtherWeightFee(form.value);
                if("firstCostFee".equals(form.field))fees.setFirstCostFee(form.value);
                if("otherCostFee".equals(form.field))fees.setOtherCostFee(form.value);
                upsert2(fees);
            }else {
                fee.setSchoolDeliveryCompanyId(exp.getId());
                fee.setProvinceId(form.id);
                fee.setProvinceName(form.provinceName);
                if("firstWeightFee".equals(form.field)){fee.setFirstWeightFee(form.value);}
                if("otherWeightFee".equals(form.field)){fee.setOtherWeightFee(form.value);}
                if("firstCostFee".equals(form.field)){fee.setFirstCostFee(form.value);}
                if("otherCostFee".equals(form.field)){fee.setOtherCostFee(form.value);}
                upsert2(fee);
                 }
            }else{
                ExpSchoolDeliveryCompany exp1 = new ExpSchoolDeliveryCompany();
                exp1.setSchoolId(form.schoolId);
                exp1.setDeliveryId(form.deliveryId);
                exp1 = upsert2(exp1);
                ExpProvinceServiceFee fees = new ExpProvinceServiceFee();
                fees.setSchoolDeliveryCompanyId(exp1.getId());
                fees.setProvinceId(form.id);
                fees.setProvinceName(form.provinceName);
                if("firstWeightFee".equals(form.field))fees.setFirstWeightFee(form.value);
                if("otherWeightFee".equals(form.field))fees.setOtherWeightFee(form.value);
                if("firstCostFee".equals(form.field))fees.setFirstCostFee(form.value);
                if("otherCostFee".equals(form.field))fees.setOtherCostFee(form.value);
                upsert2(fees);
            }
        return exp;
        }

    /**
     * @Description:快递费新增列表
     * @Author: hanchao
     * @Date: 2018/1/15 0015
     */
    public List<ExpArea> findAddProvinceServiceFeePageList(PageQuery pageQuery) {
        Criterion cri = Restrictions.eq("type",1);
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "asc";
        pageQuery.sort = "id";
        return hibernateReadonlyRepository.getList(ExpArea.class, pageQuery);
    }

    /**
     * @Description:快递费维护编辑保存
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    public ExpSchoolDeliveryCompany updateProvinceServiceFee(ExpDeliveryCompanyListForm form) throws UpsertException,ResponseEntityException {
        ExpSchoolDeliveryCompany exp = get2(ExpSchoolDeliveryCompany.class,form.id);
        if(exp == null)
            throw new ResponseEntityException(210, "该网点信息不存在");
        exp.setSchoolId(form.schoolId);
        exp.setDeliveryId(form.deliveryId);
        for(ExpDeliveryCompanyDetailForm expDelivery : form.expDeliveryCompanyList){
            ExpProvinceServiceFee expServiceFee = get2(ExpProvinceServiceFee.class,"id",expDelivery.id,"logicDeleted",false);
            if(expServiceFee == null)
                throw new ResponseEntityException(220, "该网点快递费信息不存在");
            expServiceFee.setProvinceName(expDelivery.provinceName);
            expServiceFee.setFirstWeightFee(expDelivery.firstWeightFee);
            expServiceFee.setOtherWeightFee(expDelivery.otherWeightFee);
            expServiceFee.setFirstCostFee(expDelivery.firstCostFee);
            expServiceFee.setOtherCostFee(expDelivery.otherCostFee);
            upsert2(expServiceFee);
        }
        return upsert2(exp);
    }

    /**
     * @Description:快递费维护编辑
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    public List<ExpProvinceServiceFee> getProvinceServiceFeeList(PageQuery pageQuery,Integer id) throws UpsertException,ResponseEntityException {
        Criterion cri = Restrictions.eq("schoolDeliveryCompanyId",id);
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "asc";
        pageQuery.sort = "id";
        return hibernateReadonlyRepository.getList(ExpProvinceServiceFee.class, pageQuery);
    }

    /**
     * @Description:快递费维护列表
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    public List<ExpSchoolDeliveryCompany> findProvinceServiceFeePageList(PageQuery pageQuery,Integer deliveryId,Integer schoolId,List<Integer> schoolIdList) {
        Criterion cri = Restrictions.eq("logicDeleted",false);
        if(schoolIdList.size() > 0){
            cri = Restrictions.and(cri, Restrictions.in("schoolId", schoolIdList));
        }
        if (schoolId != null) {
            cri = Restrictions.and(cri,Restrictions.eq("schoolId", schoolId));
        }
        if (deliveryId != null) {
            cri = Restrictions.and(cri,Restrictions.eq("deliveryId", deliveryId));
        }
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        return hibernateReadonlyRepository.getList(ExpSchoolDeliveryCompany.class, pageQuery);
    }

    /**
     * @Description:上门费删除
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    public void serviceFeeDelete(Integer id) throws ResponseEntityException, UpsertException {
        ExpServiceFee exp = get2(ExpServiceFee.class,id);
        if(exp == null)
            throw new ResponseEntityException(210, "上门费信息不存在");
        exp.setLogicDeleted(true);
        upsert2(exp);
    }

    /**
     * @Description:上门费新增
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    public ExpServiceFee addServiceFee(ExpServiceFeeForm form) throws UpsertException,ResponseEntityException {
        ExpServiceFee exp = new ExpServiceFee();
        exp.setSchoolId(form.schoolId);
        exp.setFee(form.fee);
        exp.setType(form.type);
        return upsert2(exp);
    }

    /**
     * @Description:上门费编辑保存去重
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    public ExpServiceFee selectServiceFee(Integer id,Integer type,Integer schoolId) throws UpsertException,ResponseEntityException {
        Criterion cri = Restrictions.ne("id", id);
        cri = Restrictions.and(cri, Restrictions.eq("type", type));
        cri = Restrictions.and(cri, Restrictions.eq("schoolId", schoolId));
        return (ExpServiceFee) getCurrentSession().createCriteria(ExpServiceFee.class)
                .add(cri)
                .uniqueResult();
    }
    /**
     * @Description:上门费新增保存去重
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    public ExpServiceFee selectAddServiceFee(Integer type,Integer schoolId) throws UpsertException,ResponseEntityException {
        Criterion cri = Restrictions.and(Restrictions.eq("type", type));
        cri = Restrictions.and(cri, Restrictions.eq("schoolId", schoolId));
        return (ExpServiceFee) getCurrentSession().createCriteria(ExpServiceFee.class)
                .add(cri)
                .uniqueResult();
    }

    /**
     * @Description:上门费编辑保存
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    public ExpServiceFee updateServiceFee(ExpServiceFeeForm form) throws UpsertException,ResponseEntityException {
        ExpServiceFee exp = get2(ExpServiceFee.class,form.id);
        if(exp == null)
            throw new ResponseEntityException(210, "该网点上门费信息不存在");
        exp.setSchoolId(form.schoolId);
        exp.setFee(form.fee);
        exp.setType(form.type);
        return upsert2(exp);
    }

    /**
     * @Description:上门费维护列表
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    public List<ExpServiceFee> findServiceFeePageList(PageQuery pageQuery, Integer schoolId,List<Integer> schoolIdList) {
        Criterion cri = Restrictions.eq("logicDeleted",false);
        if(schoolIdList.size() > 0){
            cri = Restrictions.and(cri, Restrictions.in("schoolId", schoolIdList));
        }
        if(schoolId != null){
            cri = Restrictions.eq("schoolId", schoolId);
        }
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        return hibernateReadonlyRepository.getList(ExpServiceFee.class, pageQuery);
    }

    /**
     * @Description:快递公司新增
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    public ExpSchoolDeliveryCompany addDeliveryCompany(ExpDeliveryCompanyForm form) throws UpsertException,ResponseEntityException {
        ExpSchoolDeliveryCompany exp = new ExpSchoolDeliveryCompany();
        exp.setSchoolId(form.schoolId);
        exp.setDeliveryId(form.deliveryId);
        return upsert2(exp);
    }

    /**
     * @Description:快递公司编辑保存去重
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    public ExpSchoolDeliveryCompany selectDeliveryCompany(Integer id,Integer deliveryId,Integer schoolId) throws UpsertException,ResponseEntityException {
        Criterion cri = Restrictions.ne("id", id);
        cri = Restrictions.and(cri, Restrictions.eq("deliveryId", deliveryId));
        cri = Restrictions.and(cri, Restrictions.eq("schoolId", schoolId));
        return (ExpSchoolDeliveryCompany) getCurrentSession().createCriteria(ExpSchoolDeliveryCompany.class)
                .add(cri)
                .uniqueResult();
    }

    /**
     * @Description:快递公司新增保存去重
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    public ExpSchoolDeliveryCompany selectAddDeliveryCompany(Integer deliveryId,Integer schoolId) throws UpsertException,ResponseEntityException {
        Criterion cri = Restrictions.and(Restrictions.eq("deliveryId", deliveryId));
        cri = Restrictions.and(cri, Restrictions.eq("schoolId", schoolId));
        cri = Restrictions.and(cri, Restrictions.eq("logicDeleted", false));
        return (ExpSchoolDeliveryCompany) getCurrentSession().createCriteria(ExpSchoolDeliveryCompany.class)
                .add(cri)
                .uniqueResult();
    }

    /**
     * @Description:快递公司编辑保存
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    public ExpSchoolDeliveryCompany updateDeliveryCompany(ExpDeliveryCompanyForm form) throws UpsertException,ResponseEntityException {
        ExpSchoolDeliveryCompany exp = get2(ExpSchoolDeliveryCompany.class,form.id);
        if(exp == null)
            throw new ResponseEntityException(210, "快递公司信息不存在");
        exp.setSchoolId(form.schoolId);
        exp.setDeliveryId(form.deliveryId);
        return upsert2(exp);
    }

    /**
     * @Description:快递公司逻辑删除
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    public void deliveryCompanyDelete(Integer id) throws ResponseEntityException, UpsertException {
        ExpSchoolDeliveryCompany exp = get2(ExpSchoolDeliveryCompany.class,id);
        if(exp == null)
            throw new ResponseEntityException(210, "快递公司信息不存在");
        exp.setLogicDeleted(true);
        upsert2(exp);
    }

    /**
     * @Description:快递公司维护列表
     * @Author: hanchao
     * @Date: 2018/1/4 0004
     */
    public List<ExpSchoolDeliveryCompany> findSendManagePageList(PageQuery pageQuery,Integer schoolId,List<Integer> schoolIdList) {
        Criterion cri = Restrictions.eq("logicDeleted",false);
        if(schoolIdList.size() > 0){
            cri = Restrictions.and(cri, Restrictions.in("schoolId", schoolIdList));
        }
        if(schoolId != null){
            cri = Restrictions.eq("schoolId", schoolId);
        }
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        return hibernateReadonlyRepository.getList(ExpSchoolDeliveryCompany.class, pageQuery);
    }

}
