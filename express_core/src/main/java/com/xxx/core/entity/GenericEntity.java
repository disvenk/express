package com.xxx.core.entity;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class GenericEntity extends BaseEntity<Integer> {
    public GenericEntity() {
        super();
    }
}
