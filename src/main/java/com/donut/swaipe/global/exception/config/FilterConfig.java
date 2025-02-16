package com.donut.swaipe.global.exception.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean(name = "errorTrackingFilterRegistration")  // Bean 이름 변경
    public FilterRegistrationBean<ErrorTrackingFilter> errorTrackingFilter() {
        FilterRegistrationBean<ErrorTrackingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ErrorTrackingFilter());
        registrationBean.setOrder(1);
        return registrationBean;
    }
}