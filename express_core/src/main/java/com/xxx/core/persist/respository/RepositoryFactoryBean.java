package com.xxx.core.persist.respository;

/**
 * Created by wanghua on 17/1/19.
 */
public class RepositoryFactoryBean {
    private Class clazz;

    public RepositoryFactoryBean(Class clazz) {
        this.clazz = clazz;
    }

    public Repository create() {
        Repository repository = null;
        try {
            repository = (Repository) clazz.newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return repository;
    }
}
