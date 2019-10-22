package com.xxx.core.query;

import org.hibernate.criterion.Criterion;

import java.util.HashMap;

public class AliasMappingWrapper {
    public Criterion criterion;  //已别名化关联实体查询
    public HashMap<String, String> aliasMapping = new HashMap<String, String>();  //key为别名的原始名称，value为别名

    public Criterion getCriterion() {
        return criterion;
    }

    public void setCriterion(Criterion criterion) {
        this.criterion = criterion;
    }

    public HashMap<String, String> getAliasMapping() {
        return aliasMapping;
    }

    public void setAliasMapping(HashMap<String, String> aliasMapping) {
        this.aliasMapping = aliasMapping;
    }
}
