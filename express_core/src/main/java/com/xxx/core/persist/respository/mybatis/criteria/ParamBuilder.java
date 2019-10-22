package com.xxx.core.persist.respository.mybatis.criteria;

import java.util.HashMap;
import java.util.Map;

public class ParamBuilder {

    private Map<String, Object> map = new HashMap<>();

    private ParamBuilder(){}

    public static ParamBuilder builder(){
        return new ParamBuilder();
    }

    public ParamBuilder put(String name, Object value){
        map.put(name, value);
        return this;
    }

    public Map<String, Object> build(){
        return map;
    }

}
