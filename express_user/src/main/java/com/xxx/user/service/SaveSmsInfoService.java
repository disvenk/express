package com.xxx.user.service;

import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.service.CommonService;
import com.xxx.model.business.ExpSmsMessageLog;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: disvenk.dai
 * @Date: 上午 10:42 2018/1/31 0031
 */

@Service
public class SaveSmsInfoService extends CommonService{

    /**
     * @Description:保存发送短信记录
     * @Author: disvenk.dai
     * @Date: 上午 10:42 2018/1/31 0031
     */
    public ExpSmsMessageLog saveSmsInfo(Integer schoolId,String receiverTel,String validateCode,String smsContent,String result) throws UpsertException {
        ExpSmsMessageLog expSmsMessageLog = new ExpSmsMessageLog();
        expSmsMessageLog.setSchoolId(schoolId);
        expSmsMessageLog.setUserMobile(receiverTel);
        expSmsMessageLog.setValidateCode(validateCode);
        expSmsMessageLog.setSendContent(smsContent);
        expSmsMessageLog.setSendResult(result);
       return upsert2(expSmsMessageLog);
    }
}
