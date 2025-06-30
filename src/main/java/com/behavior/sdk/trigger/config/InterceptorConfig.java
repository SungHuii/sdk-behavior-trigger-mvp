package com.behavior.sdk.trigger.config;

import com.behavior.sdk.trigger.common.interceptor.ProjectDomainValidationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    /*
    * 이 설정 클래스는 프로젝트 도메인 검증 인터셉터를 등록합니다.
    * 인터셉터는 API 요청 시 프로젝트 ID와 도메인이 일치하는지 검증합니다.
    * 이 검증은 로그 관련 API에만 적용됩니다.
    * */

    private final ProjectDomainValidationInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/api/logs/**"); // 로그 관련 API에만 적용
    }
}
