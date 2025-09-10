package com.behavior.sdk.trigger.config;

import com.behavior.sdk.trigger.common.filter.TraceIdFilter;
import com.behavior.sdk.trigger.common.interceptor.ProjectDomainValidationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private Environment env;

    private final ProjectDomainValidationInterceptor interceptor;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/api/logs"); // 로그 관련 API에만 적용
    }

    /*
    * ExceptionHandler 쪽 TraceId 주입을 위한 필터 등록
    * 명시 등록 (추천: 순서/제외 경로 제어가 명확)
    * */
    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilterRegistration() {
        FilterRegistrationBean<TraceIdFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new TraceIdFilter());
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE); // 아주 앞에서 실행
        reg.addUrlPatterns("/*");
        return reg;
    }
}
