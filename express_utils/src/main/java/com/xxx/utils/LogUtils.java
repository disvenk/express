package com.xxx.utils;

import org.apache.log4j.*;

import java.text.MessageFormat;

/**
 * 日志工具类
 */
public class LogUtils {
    /**
     * 获取根日志对象
     *
     * @return
     */
    public static Logger getRootLogger() {
        return LogManager.getRootLogger();
        //return (Logger) LogManager.getLogger("myRootLogger");  //获取一个名为 myRootLogger 的 Logger对象用于写日志
    }

    /**
     * 格式化错误信息
     *
     * @param e
     * @param className  类名称
     * @param methodName 方法名称
     */
    public static void errorFormat(Throwable e, String className, String methodName) {
        String errMsg = MessageFormat.format("{0}.{1}.{2} 抛异常：{3}",
                className,
                methodName,
                ExceptionUtils.getLineNumber(e),
                ExceptionUtils.getRootCauseMessage(e));
        errMsg = errMsg.replace("\"", "'");
        LogUtils.getRootLogger().error(errMsg);
    }
}
