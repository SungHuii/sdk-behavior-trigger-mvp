/*
package com.behavior.sdk.trigger.integration.epic2;

import com.behavior.sdk.trigger.common.interceptor.ProjectDomainValidationInterceptor;
import com.behavior.sdk.trigger.condition.controller.ConditionController;
import com.behavior.sdk.trigger.condition.dto.ConditionCreateRequest;
import com.behavior.sdk.trigger.condition.dto.ConditionResponse;
import com.behavior.sdk.trigger.condition.service.ConditionService;
import com.behavior.sdk.trigger.config.TestSecurityConfig;
import com.behavior.sdk.trigger.log_event.enums.EventType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = ConditionController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class ConditionControllerWebTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockitoBean ConditionService conditionService; // ✅ 서비스 Mock
    @MockitoBean
    ProjectDomainValidationInterceptor projectDomainValidationInterceptor;


    @Test
    @WithMockUser // ✅ 보안 통과
    void create_list_delete_flow() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID conditionId = UUID.randomUUID();

        var createReq = ConditionCreateRequest.builder()
                .projectId(projectId)
                .eventType(EventType.PAGE_VIEW)
                .operator("GRATER_THAN") // (오탈자 그대로 사용 중이면 그대로 둠; enum 전환 권장)
                .threshold(60)
                .pageUrl("https://example.com/event")
                .build();

        var created = ConditionResponse.builder()
                .id(conditionId)
                .projectId(projectId)
                .eventType(EventType.PAGE_VIEW) // ✅ 네 서비스는 enum을 그대로 넣음
                .operator("GRATER_THAN")
                .threshold(60)
                .pageUrl("https://example.com/event")
                .build();

        when(conditionService.createCondition(any())).thenReturn(created);
        when(conditionService.listConditions(projectId)).thenReturn(List.of(created));
        when(projectDomainValidationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        doNothing().when(conditionService).softDeleteCondition(conditionId);

        // create
        mvc.perform(post("/api/conditions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(conditionId.toString()))
                .andExpect(jsonPath("$.projectId").value(projectId.toString()))
                // ✅ enum 직렬화 기본값은 "PAGE_VIEW" (커스텀 설정 없다면)
                .andExpect(jsonPath("$.eventType").value("PAGE_VIEW"))
                .andExpect(jsonPath("$.operator").value("GRATER_THAN"))
                .andExpect(jsonPath("$.threshold").value(60))
                .andExpect(jsonPath("$.pageUrl").value("https://example.com/event"));

        // list
        mvc.perform(get("/api/conditions/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(conditionId.toString()))
                .andExpect(jsonPath("$[0].eventType").value("PAGE_VIEW"));

        // delete (soft)
        mvc.perform(delete("/api/conditions/{conditionId}", conditionId))
                .andExpect(status().isNoContent());

        // ✅ 서비스 위임 검증
        verify(conditionService).createCondition(any());
        verify(conditionService).listConditions(eq(projectId));
        verify(conditionService).softDeleteCondition(eq(conditionId));
        verifyNoMoreInteractions(conditionService);
    }
}
*/
