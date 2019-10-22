package com.xxx.utils.contract;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Created by wangh on 2015/11/18.
 */
public class SimpleResultExpand extends  SimpleResult {
    public Object value;

    public SimpleResultExpand(){}

    public SimpleResultExpand(boolean success, String message, Object value) {
        this.success = success;
        this.message = message;
        this.value = value;
    }


    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        //如果出现"$ref"会导致grid等控件无法加载数据。因此这里禁止循环引用检测。如果遇到循环引用，请自行调整模型。
        return JSON.toJSONString(this, SerializerFeature.WriteNullStringAsEmpty,SerializerFeature.DisableCircularReferenceDetect);
    }
}
