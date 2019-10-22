package com.xxx.core.security;

import com.xxx.core.response.CrossOriginHelper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        //response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");   //这种 responseText 中会返回 html 数据

        response = CrossOriginHelper.allowCrossOrigin(response);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}