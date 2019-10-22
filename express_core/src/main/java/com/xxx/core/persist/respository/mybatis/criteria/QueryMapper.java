package com.xxx.core.persist.respository.mybatis.criteria;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface QueryMapper {
    Class targetEntity() default void.class;

    String mappedBy() default "";

}
