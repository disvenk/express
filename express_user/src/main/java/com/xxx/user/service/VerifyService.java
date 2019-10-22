package com.xxx.user.service;

import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.service.CommonService;
import com.xxx.user.cache.VerifyCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class VerifyService extends CommonService {

    /**
     * @Description: 校验验证码是否正确
     * @Author: Chen.zm
     * @Date: 2017/11/16 0016
     */
    public void checkVerificationCode(String phoneNumber, String verificationCode) throws ResponseEntityException {
        String cacheCode = VerifyCache.getVerificationCode(phoneNumber);
        if (StringUtils.isBlank(cacheCode))
            throw new ResponseEntityException(300, "验证码已过期，请重新获取");
        if (!cacheCode.equals(verificationCode))
            throw new ResponseEntityException(300, "验证码不正确");
    }

}
