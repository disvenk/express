package com.xxx.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import java.util.List;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/favicon.ico", "/resources/**","/html/**", "/assets/**");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .inMemoryAuthentication()
//                .withUser("user").password("password").roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        MyRequestMatcher myRequestMatcher = new MyRequestMatcher();

        List<String> permitAllList = SecurityUtils.getPermitAllRequest();
        //List<String> permitAllList = FileReaderUtils.readFileByLines("/permitAllRequest");
        String[] permitAllArray = permitAllList.toArray(new String[permitAllList.size()]);

        http.
//                authorizeRequests().
//                requestMatchers(myRequestMatcher).permitAll().
//                antMatchers(permitAllArray).permitAll().anyRequest().
//                authenticated().and().addFilter(headerAuthenticationFilter()).
//                csrf().disable().headers().frameOptions().sameOrigin(); //允许跨域请求伪造

//                csrf().disable().   //禁用跨域请求伪造
                csrf().disable().headers().frameOptions().sameOrigin().and().   //允许跨域请求伪造
                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
                and().
                authorizeRequests().
                requestMatchers(myRequestMatcher).permitAll().
                antMatchers(permitAllArray).permitAll().
                //antMatchers("/").permitAll().
                //antMatchers( "/admin/**").hasRole("ADMIN" ).
                //antMatchers( "/db/**").access("hasRole('ADMIN') and hasRole('DBA')").  //任何以"/db"开头的请求同时要求用户具有"ROLE_ADMIN"和"ROLE_DBA"角色
                        anyRequest().
                authenticated().  //任何没有匹配上的其他的url请求，只需要用户被验证
                and().
                addFilter(headerAuthenticationFilter()).
                exceptionHandling().
                authenticationEntryPoint(new RestAuthenticationEntryPoint());

    }

    //KanBanPreAuthenticationFilter 过滤器在获得Header中的Token后，这里会尝试去认证用户
    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.authenticationProvider(preAuthenticationProvider());
    }

    private AuthenticationProvider preAuthenticationProvider() {
        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(new MyAuthenticationUserDetailsService());
        return provider;
    }

    @Bean
    public MyPreAuthenticationFilter headerAuthenticationFilter() throws Exception {
        return new MyPreAuthenticationFilter(authenticationManager());
    }
}
