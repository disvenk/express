package com.xxx.admin.service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.dao.SuplierDao;
import com.xxx.admin.form.*;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.ExtFilter;
import com.xxx.core.query.PageQuery;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.*;
import com.xxx.user.service.UploadFileService;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
public class ReceiveManageService extends CommonService {
    @Autowired
    private SuplierDao suplierDao;

    @Autowired
    private UploadFileService uploadFileService;

    /**
     * @Description:收件订单上门人员变更
     * @Author: hanchao
     * @Date: 2018/1/6 0006
     */
    public void updateTodoorManageChange(TodoorManageForm form) throws ResponseEntityException, UpsertException {
        ExpReceiveOrder exp = get2(ExpReceiveOrder.class,"id",form.id,"logicDeleted",false);
        if(exp == null)
            throw new ResponseEntityException(210, "上门人员信息不存在");
        exp.setDelivererId(form.delivererId);
        upsert2(exp);
    }

    /**
     * @Description:推送成功后修改推送结果
     * @Author: hanchao
     * @Date: 2018/1/25 0025
     */
    public void updateExpNoticeMessage(ExpNoticeMessageForm form) throws ResponseEntityException, UpsertException {
        ExpNoticeMessageLog exp = get2(ExpNoticeMessageLog.class,"id",form.id,"logicDeleted",false);
        if(exp == null)
            throw new ResponseEntityException(210, "推送的消息不存在");
        exp.setSendResult(form.sendResult);
        exp.setCreatedDate(new Date());
        upsert2(exp);
    }

    /**
     * @Description:获取上门人员维护的派单数
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    public JSONObject findDelivererCount(Integer delivererId,Integer schoolId) throws ResponseEntityException, UpsertException {
        return suplierDao.findDelivererCount(delivererId,schoolId);
    }

    /**
     * @Description:推送历史列表
     * @Author: hanchao
     * @Date: 2018/1/6 0006
     */
    public List<ExpNoticeMessageLog> expNoticeMessagePageList(PageQuery pageQuery,String userCode,String userMobile,Integer schoolId,List<Integer> schoolIdList) {
        Criterion cri = Restrictions.eq("logicDeleted",false);
        JSONArray jsonArray = new JSONArray();
        if(schoolIdList.size() > 0){
            cri = Restrictions.and(cri, Restrictions.in("schoolId", schoolIdList));
        }
        if (StringUtils.isNotBlank(userCode)) {
            ExtFilter filter = new ExtFilter("expUser.userCode", "string", userCode, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
        }
        if(StringUtils.isNotBlank(userMobile)){
            cri = Restrictions.and(cri,Restrictions.eq("userMobile", userMobile));
        }
        if(schoolId != null){
            cri = Restrictions.and(cri,Restrictions.eq("schoolId", schoolId));
        }
        pageQuery.filter = jsonArray.toJSONString();
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        return hibernateReadonlyRepository.getList(ExpNoticeMessageLog.class, pageQuery);
    }

    /**
     * @Description:短信历史列表
     * @Author: hanchao
     * @Date: 2018/1/6 0006
     */
    public List<ExpSmsMessageLog> expSmsMessagePageList(PageQuery pageQuery,String userCode,String userMobile,Integer schoolId,List<Integer> schoolIdList) {
        Criterion cri = Restrictions.eq("logicDeleted",false);
        JSONArray jsonArray = new JSONArray();
        if(schoolIdList.size() > 0){
//            cri = Restrictions.and(cri, Restrictions.in("schoolId", schoolIdList));
            StringBuffer ids = new StringBuffer();
            for (int i : schoolIdList) {
                ids.append(i + ",");
            }
            ExtFilter filter = new ExtFilter("expUser.schoolId", "list", ids.toString(), ExtFilter.ExtFilterComparison.in, null);
            jsonArray.add(filter);
        }
        if (StringUtils.isNotBlank(userCode)) {
            ExtFilter filter = new ExtFilter("expUser.userCode", "string", userCode, ExtFilter.ExtFilterComparison.like, null);
            jsonArray.add(filter);
        }
        if(StringUtils.isNotBlank(userMobile)){
            cri = Restrictions.and(cri,Restrictions.like("userMobile", userMobile, MatchMode.ANYWHERE));
        }
        if(schoolId != null){
//            cri = Restrictions.and(cri,Restrictions.eq("expUser.schoolId", schoolId));
            ExtFilter filter = new ExtFilter("expUser.schoolId", "string", schoolId + "", ExtFilter.ExtFilterComparison.eq, null);
            jsonArray.add(filter);
        }
        pageQuery.filter = jsonArray.toJSONString();
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        return hibernateReadonlyRepository.getList(ExpSmsMessageLog.class, pageQuery);
    }

    /**
     * @Description:上门人员删除
     * @Author: hanchao
     * @Date: 2018/1/6 0006
     */
    public void todoorManageDelete(Integer id) throws ResponseEntityException, UpsertException {
        ExpUser exp = get2(ExpUser.class,"id",id,"logicDeleted",false);
        if(exp == null)
            throw new ResponseEntityException(210, "上门人员信息不存在");
        exp.setLogicDeleted(true);
        upsert2(exp);
    }

    /**
     * @Description:上门人员新增
     * @Author: hanchao
     * @Date: 2018/1/22 0022
     */
    public void addTodoorManage(TodoorManageForm form) throws ResponseEntityException, UpsertException {
        ExpUser exp = new ExpUser();
        exp.setLoginType(2);
        exp.setUserCode(form.userCode);
        exp.setUserMobile(form.userMobile);
        exp.setSchoolId(form.schoolId);
        exp.setRemarks(form.remark);
        exp.setUserPassword(MD5Utils.md5Hex("888888"));
        upsert2(exp);
    }

    /**
     * @Description:上门人员编辑保存去重
     * @Author: hanchao
     * @Date: 2018/1/22 0022
     */
    public ExpUser selectIsTodoor(Integer id,Integer schoolId,String userMobile) throws ResponseEntityException, UpsertException {
        Criterion cri = Restrictions.eq("schoolId",schoolId);
        cri = Restrictions.and(cri,Restrictions.ne("id",id));
        cri = Restrictions.and(cri,Restrictions.eq("loginType",2));
        cri = Restrictions.and(cri,Restrictions.eq("logicDeleted",false));
        cri = Restrictions.and(cri,Restrictions.eq("userMobile",userMobile));
        return (ExpUser) getCurrentSession().createCriteria(ExpUser.class)
                .add(cri)
                .uniqueResult();
    }

    /**
     * @Description:上门人员编辑保存去重
     * @Author: hanchao
     * @Date: 2018/1/22 0022
     */
    public ExpUser selectIsTodoorAdd(Integer schoolId,String userMobile) throws ResponseEntityException, UpsertException {
        Criterion cri = Restrictions.eq("schoolId",schoolId);
        cri = Restrictions.and(cri,Restrictions.eq("loginType",2));
        cri = Restrictions.and(cri,Restrictions.eq("logicDeleted",false));
        cri = Restrictions.and(cri,Restrictions.eq("userMobile",userMobile));
        return (ExpUser) getCurrentSession().createCriteria(ExpUser.class)
                .add(cri)
                .uniqueResult();
    }

    /**
     * @Description:上门人员维护编辑保存
     * @Author: hanchao
     * @Date: 2018/1/6 0006
     */
    public void updateTodoorManage(TodoorManageForm form) throws ResponseEntityException, UpsertException {
        ExpUser exp = get2(ExpUser.class,form.id);
        if(exp == null)
            throw new ResponseEntityException(210, "上门人员信息不存在");
        exp.setUserCode(form.userCode);
        exp.setUserMobile(form.userMobile);
        exp.setSchoolId(form.schoolId);
        exp.setRemarks(form.remark);
        upsert2(exp);
    }

    /**
     * @Description:上门人员维护列表
     * @Author: hanchao
     * @Date: 2018/1/6 0006
     */
    public List<ExpUser> findTodoorManagePageList(PageQuery pageQuery,String userCode,String userMobile,Integer schoolId,List<Integer> schoolIdList) {
        Criterion cri = Restrictions.eq("loginType",2);
        if(schoolIdList.size() > 0){
            cri = Restrictions.and(cri, Restrictions.in("schoolId", schoolIdList));
        }
        if (StringUtils.isNotBlank(userCode)) {
            cri = Restrictions.and(cri,Restrictions.like("userCode", userCode, MatchMode.ANYWHERE));
        }
        if (StringUtils.isNotBlank(userMobile)) {
            cri = Restrictions.and(cri,Restrictions.like("userMobile", userMobile, MatchMode.ANYWHERE));
        }
        if(schoolId != null){
            cri = Restrictions.and(cri,Restrictions.eq("schoolId", schoolId));
        }
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        return hibernateReadonlyRepository.getList(ExpUser.class, pageQuery);
    }

    /**
     * @Description:收件订单里的上门人员维护
     * @Author: hanchao
     * @Date: 2018/1/6 0006
     */
    public ExpReceiveOrder updateTodoorPerson(Integer id,Integer delivererId) throws ResponseEntityException, UpsertException {
        ExpReceiveOrder exp = get2(ExpReceiveOrder.class,id);
        if(exp == null)
            throw new ResponseEntityException(210, "订单信息不存在");
        exp.setDelivererId(delivererId);
        return upsert2(exp);
    }

    /**
     * @Description:判断是否存在本地用户
     * @Author: hanchao
     * @Date: 2018/1/6 0006
     */
    public List<ExpReceiveOrder> selectIsThisUser(Integer id,String receiverTel,Integer schoolId) throws ResponseEntityException, UpsertException {
        Criterion cri = Restrictions.eq("receiverTel",receiverTel);
        cri = Restrictions.and(cri,Restrictions.ne("id",id));
        cri = Restrictions.and(cri,Restrictions.eq("schoolId",schoolId));
        return (List<ExpReceiveOrder>) getCurrentSession().createCriteria(ExpReceiveOrder.class)
                .add(cri)
                .list();
    }

    /**
     * @Description:上门人员列表
     * @Author: hanchao
     * @Date: 2018/1/6 0006
     */
    public List<ExpUser> getAllTodoorPerson(List<Integer> schoolIdList) throws ResponseEntityException, UpsertException {
        Criterion cri = Restrictions.eq("loginType",2);
        if(schoolIdList.size() > 0){
            cri = Restrictions.and(cri, Restrictions.in("schoolId", schoolIdList));
        }
        return (List<ExpUser>) getCurrentSession().createCriteria(ExpUser.class)
                .add(cri)
                .list();
    }

    /**
     * @Description:收件订单删除
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    public void expReceiveOrderDelete(Integer id) throws ResponseEntityException, UpsertException {
        ExpReceiveOrder exp = get2(ExpReceiveOrder.class,id);
        if(exp == null)
            throw new ResponseEntityException(210, "订单信息不存在");
        exp.setLogicDeleted(true);
        upsert2(exp);
    }

    /**
     * @Description:收件订单编辑保存
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    public ExpReceiveOrder updateExpReceiveOrder(ExpReceiveOrderForm form) throws UpsertException,ResponseEntityException {
        ExpReceiveOrder exp = get2(ExpReceiveOrder.class,form.id);
        if(exp == null)
            throw new ResponseEntityException(210, "该订单信息不存在");
//        if(StringUtils.isNotBlank(form.deliveryNo)){
//            ExpSchool expSchool = get2(ExpSchool.class,"id",exp.getSchoolId(),"logicDeleted",false);
//            if(expSchool.getIspaper() == 0){
//                Criterion cri = Restrictions.and(Restrictions.eq("logicDeleted", false));
//                cri = Restrictions.and(cri,Restrictions.eq("deliveryNo", form.deliveryNo));
//                cri = Restrictions.and(cri,Restrictions.eq("deliveryId", exp.getDeliveryId()));
//                cri = Restrictions.and(cri,Restrictions.eq("schoolId",exp.getSchoolId()));
//                ExpDeliveryOrderNo expDeliveryOrderNo = (ExpDeliveryOrderNo) getCurrentSession().createCriteria(ExpDeliveryOrderNo.class)
//                        .add(cri)
//                        .uniqueResult();
//                if(expDeliveryOrderNo == null){
//                    throw new ResponseEntityException(220, "你输入的快递单号在系统没有维护");
//                }
//                expDeliveryOrderNo.setUsed(true);
//                upsert2(expDeliveryOrderNo);
//            }
//        }
//        exp.setDeliveryNo(form.deliveryNo);
        exp.setReceiverName(form.receiverName);
        exp.setReceiverTel(form.receiverTel);
        exp.setReceiverAddress(form.receiverAddress);
        exp.setDelivererId(form.delivererId);
        exp.setProvince(form.province);
        exp.setCity(form.city);
        exp.setZone(form.zone);
//        exp.setCapture(uploadFileService.saveOssUploadFileByBase64(form.captureKey).toString());
        return upsert2(exp);
    }

    /**
     * @Description:获取收件订单的费用(派件上门)
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    public List<JSONObject> findReceiveOrderCount(JSONObject pageQuery) throws ResponseEntityException, UpsertException {
        return suplierDao.findReceiveOrderCount(pageQuery);
    }
    /**
     * @Description:获取收件订单的费用(自行取件)
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    public List<JSONObject> findReceiveQtyCount(JSONObject pageQuery) throws ResponseEntityException, UpsertException {
        return suplierDao.findReceiveQtyCount(pageQuery);
    }

    /**
     * @Description:收件订单列表
     * @Author: hanchao
     * @Date: 2018/1/6 0006
     */
    public List<ExpReceiveOrder> findReceiveOrderPageList(PageQuery pageQuery,Boolean isDelete,String userCode,String userMobile,Integer type,Integer orderStatus,
                                                          Integer delivererId,String orderStartTime,String orderEndTime,String expressStartTime,String expressEndTime,
                                                          Integer schoolId,List<Integer> schoolIdList, String orderNo, String deliveryNo) throws ParseException {
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
        if(delivererId != null){
            cri = Restrictions.and(cri,Restrictions.eq("delivererId", delivererId));
        }
        if(schoolId != null){
            cri = Restrictions.and(cri,Restrictions.eq("schoolId", schoolId));
        }
        if(StringUtils.isNotBlank(orderNo)){
            cri = Restrictions.and(cri,Restrictions.like("orderNo", orderNo, MatchMode.ANYWHERE));
        }
        if(StringUtils.isNotBlank(deliveryNo)){
            cri = Restrictions.and(cri,Restrictions.like("deliveryNo", deliveryNo, MatchMode.ANYWHERE));
        }
        if (StringUtils.isNotBlank(orderStartTime)) {
            cri = Restrictions.and(cri,Restrictions.ge("arrivedDate", DateTimeUtils.parseDate(orderStartTime,"yyyy-MM-dd")));
        }
        if (StringUtils.isNotBlank(orderEndTime)) {
            cri = Restrictions.and(cri,Restrictions.le("arrivedDate", DateTimeUtils.parseDate(orderEndTime,"yyyy-MM-dd")));
        }
        if (StringUtils.isNotBlank(expressStartTime)) {
            cri = Restrictions.and(cri,Restrictions.ge("expressDate", DateTimeUtils.parseDate(expressStartTime,"yyyy-MM-dd")));
        }
        if (StringUtils.isNotBlank(expressEndTime)) {
            cri = Restrictions.and(cri,Restrictions.le("expressDate", DateTimeUtils.parseDate(expressEndTime,"yyyy-MM-dd")));
        }
        if(isDelete != null){
            pageQuery.logicDeleted = isDelete;
        }
        pageQuery.filter = jsonArray.toJSONString();
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        return hibernateReadonlyRepository.getList(ExpReceiveOrder.class, pageQuery);
    }

    /**
     * @Description:到件信息列表
     * @Author: hanchao
     * @Date: 2018/1/5 0005
     */
    public List<ExpReceiveOrder> findArriveMessagePageList(PageQuery pageQuery,String receiverTel,Integer schoolId,List<Integer> schoolIdList, String deliveryNo) {
        Criterion cri = Restrictions.eq("logicDeleted",false);
        if(schoolIdList.size() > 0){
            cri = Restrictions.and(cri, Restrictions.in("schoolId", schoolIdList));
        }
        if (StringUtils.isNotBlank(receiverTel)) {
            cri = Restrictions.and(cri,Restrictions.like("receiverTel", receiverTel,MatchMode.ANYWHERE));
        }
        if (StringUtils.isNotBlank(deliveryNo)) {
            cri = Restrictions.and(cri,Restrictions.like("deliveryNo", deliveryNo,MatchMode.ANYWHERE));
        }
        if(schoolId != null){
            cri = Restrictions.and(cri,Restrictions.eq("schoolId", schoolId));
        }
        pageQuery.hibernateCriterion = cri;
        pageQuery.order = "desc";
        pageQuery.sort = "id";
        return hibernateReadonlyRepository.getList(ExpReceiveOrder.class, pageQuery);
    }

}
