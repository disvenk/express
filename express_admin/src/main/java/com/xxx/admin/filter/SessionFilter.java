//package com.xxx.admin.filter;
//
//import com.xxx.user.utils.SessionUtils;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//
//public class SessionFilter implements Filter{
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        //判断session是否过期
//        if (SessionUtils.getSession(request,"USER_ID") == null) {
//            String errors = "您还没有登录，或者session已过期。请先登陆!";
//            request.setAttribute("Message", errors);
//            System.out.println(errors);
////            response.sendRedirect("../home/welcomeHtml");
////            return;
//            //跳转至登录页面
////            request.getRequestDispatcher("/home.html").forward(request, response);
//        } else {
//            filterChain.doFilter(request, response);
//        }
//    }
//
//    @Override
//    public void destroy() {
//
//    }
//}
