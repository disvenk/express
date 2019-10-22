package com.xxx.core.filter.gzip;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GZipFilter implements Filter {

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String acceptEncoding = request.getHeader("Accept-Encoding");
        //System.out.println("Accept-Encoding: " + acceptEncoding);

        if (acceptEncoding != null && acceptEncoding.toLowerCase().indexOf("gzip") != -1) {
            GZipResponseWrapper gzipResponse = new GZipResponseWrapper(response);  //若浏览器支持 gzip 编码，则使用 gzip 压缩数据
            chain.doFilter(request, gzipResponse); //第二个参数是自定义的 Response
            gzipResponse.finishResponse();
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
    }
}