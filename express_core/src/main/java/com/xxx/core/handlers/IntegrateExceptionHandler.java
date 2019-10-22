package com.xxx.core.handlers;

import com.alibaba.fastjson.JSONObject;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.core.exceptions.ResponseEntityException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class IntegrateExceptionHandler implements HandlerExceptionResolver {
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String stackTrace = "";
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            ex.printStackTrace(new PrintWriter(buf, true));
            stackTrace = buf.toString();
            buf.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        String accept = request.getHeader("accept");
        String contentType = request.getHeader("Content-Type");
        String requestHeader = request.getHeader("X-Requested-With");
        if ((accept != null && accept.contains("application/json")) || (requestHeader != null && requestHeader.contains("XMLHttpRequest"))||request.getMethod().equalsIgnoreCase("POST")) {
            try {
                // JSON格式返回，返回的结果类似于：return new ResponseEntity(jo, HttpStatus.INTERNAL_SERVER_ERROR)
                response.setCharacterEncoding("utf-8");
                response.setContentType("application/json");
                PrintWriter writer = response.getWriter();

                if (ex instanceof ResponseEntityException) {
                    ResponseEntityException rex = (ResponseEntityException) ex;
                    RestResponseEntity restResponseEntity = new RestResponseEntity(rex.getResponseStatus(), rex.getMessage(), null);

                    response.setStatus(200);
                    writer.write(JSONObject.toJSONString(restResponseEntity));
                } else {
                    JSONObject errorWraper = new JSONObject();
                    errorWraper.put("exceptionMessage", ex.getMessage());
                    errorWraper.put("exceptionType", ex.getClass().toString());
                    errorWraper.put("stackTrace", stackTrace);

                    response.setStatus(500);
                    writer.write(JSONObject.toJSONString(errorWraper));
                }

                writer.flush();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
//                return new ModelAndView("404");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
