package com.behavior.sdk.trigger.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

/*
 * OpenApiCustomizer로 모든 Operation에 표준 에러 응답을 자동 추가
 */
@Configuration
public class OpenApiErrorResponsesConfig {

    // 공통 ErrorResponse 스키마 참조 등록
    @Bean
    public OpenAPI errorResponseSchema(OpenAPI openAPI) {
        Components components = openAPI.getComponents() != null ? openAPI.getComponents() : new Components();
        components.addSchemas("ErrorResponse", new Schema<>().$ref("#/components/schemas/ErrorResponse"));
        openAPI.setComponents(components);
        return openAPI;
    }

    // 모든 엔드포인트에 공통 에러 응답 추가
    @Bean
    public OpenApiCustomizer globalErrorResponsesCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
            pathItem.readOperations().forEach(op -> {
                ApiResponses rs = op.getResponses();
                // 공통으로 넣을 상태코드 목록
                add(rs, "400", "Bad Request");
                add(rs, "401", "Unauthorized");
                add(rs, "403", "Forbidden");
                add(rs, "404", "Not Found");
                add(rs, "405", "Method Not Allowed");
                add(rs, "415", "Unsupported Media Type");
                add(rs, "422", "Unprocessable Entity");
                add(rs, "429", "Too Many Requests");
                add(rs, "500", "Internal Server Error");
            })
        );
    }

    private void add(ApiResponses rs, String code, String desc) {
        // 이미 개별 Operation에서 정의했다면 덮어쓰지 않음
        if (rs.containsKey(code)) return;

        ApiResponse resp = new ApiResponse().description(desc);

        Content content = new Content();
        MediaType mediaType = new MediaType()
                .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"));

        content.put("application/json", mediaType);

        resp.setContent(content);
        rs.addApiResponse(code, resp);
    }
}
