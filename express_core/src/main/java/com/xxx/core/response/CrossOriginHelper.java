package com.xxx.core.response;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wanghua on 17/2/5.
 */
public class CrossOriginHelper {
    /**
     * 允许跨域设置
     *
     * @param servletResponse
     * @return
     */
    public static HttpServletResponse allowCrossOrigin(ServletResponse servletResponse) {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader("Access-Control-Allow-Origin", "*");  // 表明它允许"http://xxx"发起跨域请求
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Max-Age", "3628800");  // 表明在xxx秒内，不需要再发送预检验请求，可以缓存该结果
        response.setHeader("Access-Control-Allow-Headers", "Authorization,DNT,User-Agent,Keep-Alive,Content-Type,accept,origin,X-Requested-With");
        return response;
    }
}
