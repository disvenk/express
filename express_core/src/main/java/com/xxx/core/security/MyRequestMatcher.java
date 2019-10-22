package com.xxx.core.security;

import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by wanghua on 17/1/18.
 */
public class MyRequestMatcher implements RequestMatcher {
    public boolean matches(HttpServletRequest request) {
        return CorsUtils.isPreFlightRequest(request);
    }
}
