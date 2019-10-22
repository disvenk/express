package com.xxx.core.persist.session;


import java.io.Closeable;

/**
 * Created by wanghua on 17/1/21.
 */
public interface SessionSupport<T extends Closeable> {

    void openThreadSession(boolean readonly);

    T getCurrentSession();

    void close();
}
