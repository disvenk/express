package com.xxx.core.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * 预认证
 */
public class MyPreAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {
    //public static final String SSO_TOKEN = "X-Auth-Token";
    public static final String HTTP_BASIC_AUTHORIZATION = "Authorization";
    public static final String SSO_CREDENTIALS = "N/A";

    public MyPreAuthenticationFilter(AuthenticationManager authenticationManager) {
        setAuthenticationManager(authenticationManager);
    }

    //过滤器在获得Header中的Token后，Spring Security会尝试去认证用户。
    //这里，如果 return null，会进入 RestAuthenticationEntryPoint，不会再进入 MyAuthenticationUserDetailsService.loadUserDetails 了。
    //注意：css、js等静态资源的请求若不处理下，也会触发此函数的执行。
    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        //String uri = request.getRequestURI().toString();
        String auth = request.getHeader(HTTP_BASIC_AUTHORIZATION);  //使用者身份
        if (StringUtils.isBlank(auth)) return null;

        String[] sAuth = auth.split(" ");
        if (sAuth.length != 2) {
            return null;
        } else {
            if (!sAuth[0].equals("Basic")) return null;
            String token = sAuth[1];
            if (StringUtils.isBlank(token)) return null;
            return token;
        }
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return SSO_CREDENTIALS;
    }
}