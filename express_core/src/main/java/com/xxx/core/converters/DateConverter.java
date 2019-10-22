package com.xxx.core.converters;

import java.util.Date;

import com.xxx.utils.DateTimeUtils;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by wangh on 2015/11/5.*/

public class DateConverter implements Converter<String, Date> {

    /**
     * 实体中必须有 Date 字段，此方法才会被执行。
     *
     * @param source
     * @return
     */
    @Override
    public Date convert(String source) {
        return DateTimeUtils.convertToDate(source);
    }
}
