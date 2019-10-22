package com.xxx.user.filter;

import com.xxx.core.response.CrossOriginHelper;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by wanghua on 16/12/16.
 * @comment 跨域过滤器
 */
public class CrossOriginFilter implements Filter {

    private FilterConfig config = null;

    public void init(FilterConfig config) throws ServletException {
        this.config = config;
    }

    public void destroy() {
        this.config = null;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, CrossOriginHelper.allowCrossOrigin(response));
    }
}