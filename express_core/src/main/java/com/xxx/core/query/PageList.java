package com.xxx.core.query;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by wanghua on 17/4/14.
 */
public class PageList<T>  extends ArrayList<T> {
    public long total;

    public PageList(Collection<? extends T> c, long total) {
        super(c);
        this.total = total;
    }
}
