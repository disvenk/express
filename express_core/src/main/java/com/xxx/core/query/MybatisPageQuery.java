package com.xxx.core.query;

import com.github.pagehelper.PageRowBounds;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanghua on 17/2/15.
 */
public class MybatisPageQuery extends PageRowBounds {
    private Map params = new HashMap();

    public MybatisPageQuery(int offset) {
        super(offset, 10);
    }

    public MybatisPageQuery() {
        this(1);
    }

    public MybatisPageQuery(int offset, int limit) {
        super(offset, limit);
    }

    public Map getParams() {
        return params;
    }

    public void setParams(Map params) {
        this.params = params;
    }


}
