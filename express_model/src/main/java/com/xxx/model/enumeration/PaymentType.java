package com.xxx.model.enumeration;

import com.xxx.core.entity.CodeBaseEnum;

/**
 * 支付类型（UPMP银联、WECHAT微信、ALIPAY 支付宝、CASH现金支付）
 */
public enum PaymentType implements CodeBaseEnum {
    UPMP, WECHAT, ALIPAY,CASH;

    private int index;
    private String name;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    PaymentType() {
        this.index = ordinal();
        this.name = this.toString();
    }

    public static String fromIndex(int index) {
        for (PaymentType s : PaymentType.values()) {
            if (index == s.getIndex())
                return s.name;
        }
        return null;
    }


    @Override
    public int code() {
        return this.index;
    }
}