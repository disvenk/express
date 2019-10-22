//package com.shiyu.pay.service;
//
//import com.shiyu.core.service.CommonService;
//import com.shiyu.model.business.RechargeUserRecord;
//import org.hibernate.criterion.Criterion;
//import org.hibernate.criterion.Restrictions;
//import org.springframework.stereotype.Service;
//
///**
// * 用户钱包
// */
//@Service("rechargeUserRecordService2")
//public class RechargeUserRecordService extends CommonService {
//    /**
//     * 获取用户充值卡，不存在会自动创建
//     *
//     * @param brandId
//     * @param userId
//     * @return
//     */
//    public RechargeUserRecord createRechargeUserRecordIfNotExists(int brandId, int userId) {
//        Criterion cri = Restrictions.eq("userId", userId);
//        cri = Restrictions.and(cri, Restrictions.eq("brandId", brandId));
//        cri = Restrictions.and(cri, Restrictions.eq("rechargeCardType", 1));
//        RechargeUserRecord ruRecord = (RechargeUserRecord) getCurrentSession().createCriteria(RechargeUserRecord.class).add(cri).uniqueResult();
//        if (ruRecord == null) {
//            ruRecord = new RechargeUserRecord();
//            ruRecord.setUserId(userId);
//            ruRecord.setBrandId(brandId);
//            ruRecord.setRechargePrice(0d);
//            ruRecord.setPresentPrice(0d);
//            ruRecord.setConsumePrice(0d);
//            ruRecord.setBalance(0d);
//            ruRecord.setCreateTime(new Integer(String.valueOf(System.currentTimeMillis() / 1000)));
//            ruRecord.setStatus(0);
//            ruRecord.setRechargeCardType(1);
//        }
//        return ruRecord;
//    }
//}
