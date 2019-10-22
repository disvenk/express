package com.xxx.model.enumeration;

import com.xxx.core.entity.CodeBaseEnum;

public enum Status implements CodeBaseEnum {
    正常, 停用;

    private int index;
    private String name;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    Status() {
        this.index = ordinal();
        this.name = this.toString();
    }

    public static String fromIndex(int index) {
        for (Status s : Status.values()) {
            if (index == s.getIndex())
                return s.name;
        }
        return null;
    }


    @Override
    public int code() {
        return this.index;
    }
}