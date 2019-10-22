package com.xxx.core.converters;

import com.xxx.utils.DateTimeUtils;
import org.springframework.core.convert.converter.Converter;
import java.sql.Timestamp;

/**
 * Created by wangh on 2015/11/5.
 * 实体中必须有 Timestamp 字段，此方法才会被执行。
 */
public class TimestampConverter implements Converter<String, Timestamp> {
    @Override
    public Timestamp convert(String source) {
        return DateTimeUtils.convertToTimestamp(source);
    }
}
