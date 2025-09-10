package com.behavior.sdk.trigger.integration.epic4;

import com.behavior.sdk.trigger.common.exception.GlobalExceptionHandler;
import com.behavior.sdk.trigger.common.interceptor.ProjectDomainValidationInterceptor;
import com.behavior.sdk.trigger.common.security.JwtAuthenticationFilter;
import com.behavior.sdk.trigger.common.security.JwtUtils;
import com.behavior.sdk.trigger.config.InterceptorConfig;
import com.behavior.sdk.trigger.config.SecurityConfig;
import com.behavior.sdk.trigger.config.TestControllers;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ErrorSliceTest.SampleController.class,
        properties = {
        // 없으면 404가 예외로 안 들어오고 기본 처리로 끝날 수 있음
        "spring.mvc.throw-exception-if-no-handler-found=true",
        "spring.web.resources.add-mappings=false" // static 리소스 매핑 안 함
        },
        excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
            classes = {
                    ProjectDomainValidationInterceptor.class,
                    InterceptorConfig.class,
                    SecurityConfig.class,
                    JwtAuthenticationFilter.class
            })
        }
)
@AutoConfigureMockMvc(addFilters = false) // 시큐리티 필터 비활성화 (401 방지)
@Import({GlobalExceptionHandler.class, TestControllers.class}) // @ControllerAdvice 자동 스캔되지만, 명시적으로 import
public class ErrorSliceTest {

    @MockitoBean
    JwtUtils jwtUtils;

    @MockitoBean
    ProjectDomainValidationInterceptor interceptor;

    @Autowired
    MockMvc mockMvc;

    // 1. Body 검증 실패 -> 400
    @Test
    @DisplayName("Body 검증 실패 > 400 + VALID-BODY_VALIDATION_FAILED + details[]")
    void bodyValidationFailed() throws Exception {
        String json = """
                {"name": ""} // name이 @NotBlank라서 실패함
                """;

        mockMvc.perform(post("/api/sample")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALID-BODY_VALIDATION_FAILED"))
                .andExpect(jsonPath("$.numeric").value(2001));
    }

    // 2. 잘못된 HTTP 메서드
    @Test
    @DisplayName("지원하지 않는 메서드 > 405 + SYS-METHOD_NOT_ALLOWED")
    void methodNotAllowed() throws Exception {
        mockMvc.perform(post("/api/ping")) // ping은 GET만 지원
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value("SYS-METHOD_NOT_ALLOWED"))
                .andExpect(jsonPath("$.numeric").value(9002));
    }

    // 3. 존재하지 않는 URL
    @Test
    @DisplayName("존재하지 않는 URL > 404 + SYS-FILE_NOT_FOUND")
    void notFoundUrl() throws Exception {
        mockMvc.perform(get("/no-such-url"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SYS-FILE_NOT_FOUND"))
                .andExpect(jsonPath("$.numeric").value(9004));
    }

    // ---------- 테스트 전용 샘플 컨트롤러 ----------
    @RestController
    public static class SampleController {
        @GetMapping("/api/ping")
        public String ping() {
            return "pong";
        }

        @PostMapping("/api/sample")
        public String create(@Valid @RequestBody SampleRequest req) {
            return "ok";
        }
    }

    // ---------- 테스트 전용 DTO ----------
    static class SampleRequest {
        @NotBlank
        public String name;
    }
}
