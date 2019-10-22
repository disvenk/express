package com.xxx.core.persist.respository.mybatis.handler;

import com.xxx.core.entity.CodeBaseEnum;

public class CodeEnumHelper {
    /**
     * @param enumClass
     * @param code
     * @param <E>
     * @return
     */
    public static <E extends Enum<?> & CodeBaseEnum> E codeOf(Class<E> enumClass, int code) {
        E[] enumConstants = enumClass.getEnumConstants();
        for (E e : enumConstants) {
            if (e.code() == code)
                return e;
        }
        return null;
    }
}