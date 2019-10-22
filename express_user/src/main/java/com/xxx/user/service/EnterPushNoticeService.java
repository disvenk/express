package com.xxx.user.service;

import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.ExpDeliveryCompany;
import com.xxx.model.business.ExpNoticeMessageLog;
import com.xxx.model.business.ExpReceiveOrder;
import com.xxx.user.security.CurrentUser;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: disvenk.dai
 * @Date: 下午 3:01 2018/1/26 0026
 */
@Service
public class EnterPushNoticeService extends CommonService{
    /**
     * @Description: 保存录入消息
     * @Author: disvenk.dai
     * @Date: 2018/1/26
     */
    public ExpNoticeMessageLog saveNotice(Integer userId,String deliveryNo, String tel, String deliveryCompanyName,String validateCode,String content, String result) throws UpsertException {
        ExpNoticeMessageLog expNoticeMessageLog = new ExpNoticeMessageLog();
        ExpDeliveryCompany expDeliveryCompany =get2(ExpDeliveryCompany.class,"name",deliveryCompanyName);
        expNoticeMessageLog.setDeliveryId(expDeliveryCompany.getId());
        expNoticeMessageLog.setDeliveryNo(deliveryNo);
        ExpReceiveOrder expReceiveOrder = get2(ExpReceiveOrder.class,"deliveryNo",deliveryNo);
        expNoticeMessageLog.setOrderId(expReceiveOrder.getId());
        expNoticeMessageLog.setSchoolId(CurrentUser.get().schoolId);
        expNoticeMessageLog.setNoticeType(1);
        expNoticeMessageLog.setValidateCode(validateCode);
        expNoticeMessageLog.setSendContent(content);
        expNoticeMessageLog.setSendResult(result);
        expNoticeMessageLog.setUserMobile(tel);
        expNoticeMessageLog.setUserId(userId);
        return upsert2(expNoticeMessageLog);
    }
}
