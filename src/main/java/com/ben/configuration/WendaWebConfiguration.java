package com.ben.configuration;

import com.ben.interceptor.LoginRequiredInterceptor;
import com.ben.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @ClassName: WendaWebConfiguration
 * @author: benjamin
 * @version: 1.0
 * @description: TODO
 * @createTime: 2019/08/18/20:55
 */

@Component
public class WendaWebConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    PassportInterceptor passportInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor); // 在系统初始化的时候加入ticket的拦截器
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/user/*"); // 访问用户页面是被拦截的
        super.addInterceptors(registry);
    }
}

