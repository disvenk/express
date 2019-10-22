package com.xxx.core.persist.respository.mybatis.criteria;

import java.util.ArrayList;
import java.util.List;

public class CRUDExecution {

    private String where;
    private List<String> includeProperty = new ArrayList<>();
    private List<String> excludeProperty = new ArrayList<>();

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{

        CRUDExecution crudExecution;

        public Builder(){
            crudExecution = new CRUDExecution();
        }

        public Builder addIncludeProperty(String str){
            crudExecution.getIncludeProperty().add(str);
            return this;
        }

        public Builder addExcludeProperty(String str){
            crudExecution.getExcludeProperty().add(str);
            return this;
        }

        public Builder where(String where){
            crudExecution.where = where;
            return this;
        }

        public CRUDExecution build(){
            return crudExecution;
        }
    }

    public String getWhere() {
        return where;
    }

    public List<String> getIncludeProperty() {
        return includeProperty;
    }

    public List<String> getExcludeProperty() {
        return excludeProperty;
    }


}
