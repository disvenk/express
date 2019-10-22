package com.xxx.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 异常工具类
 */
public class ExceptionUtils {
    /**
     * 获取根异常
     *
     * @param e
     * @return
     */
    public static Throwable getRootCause(Throwable e) {
        Throwable root = e;
        while (root.getCause() != null)
            root = root.getCause();
        return root;
    }

    /**
     * 获取根异常信息
     *
     * @param e
     * @return
     */
    public static String getRootCauseMessage(Throwable e) {
        return getRootCause(e).getMessage();
    }

    /**
     * 获取异常堆栈信息
     *
     * @param e
     * @return
     */
    public static String getStackTraceMessage(Throwable e) {
        String stackTrace = "";
        try {
            ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
            e.printStackTrace(new java.io.PrintWriter(buf, true));
            buf.close();
            stackTrace = buf.toString();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return stackTrace;
    }

    /**
     * 获取抛出异常的代码行
     *
     * @param e
     * @return
     */
    public static int getLineNumber(Throwable e) {
        return getRootCause(e).getStackTrace()[0].getLineNumber();
    }
}
