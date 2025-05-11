package com.behavior.sdk.trigger.condition.controller;

import com.behavior.sdk.trigger.condition.dto.ConditionCreateRequest;
import com.behavior.sdk.trigger.condition.dto.ConditionResponse;
import com.behavior.sdk.trigger.condition.service.ConditionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conditions")
@RequiredArgsConstructor
@Tag(name = "Condition", description = "조건 관리 API")
public class ConditionController {

    private final ConditionService conditionService;

    @PostMapping
    @Operation(summary = "조건 생성", description = "새로운 Condition을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "조건 생성 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    public ResponseEntity<ConditionResponse> create(@RequestBody @Valid ConditionCreateRequest request) {
        ConditionResponse createdCondition = conditionService.createCondition(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCondition);
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "조건 조회", description = "프로젝트 ID로 특정 조건 정보 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조건 조회 성공")
    @ApiResponse(responseCode = "404", description = "조건을 찾을 수 없음")
    public ResponseEntity<List<ConditionResponse>> getConditionLists(@PathVariable UUID projectId) {
        return ResponseEntity.ok(conditionService.listConditions(projectId));
    }

    @DeleteMapping("/{conditionId}")
    @Operation(summary = "조건 삭제(soft delete)", description = "조건 ID로 특정 조건을 삭제(soft delete)합니다.")
    @ApiResponse(responseCode = "204", description = "조건 삭제 성공")
    @ApiResponse(responseCode = "404", description = "조건을 찾을 수 없음")
    public ResponseEntity<Void> softDeleteCondition(@PathVariable UUID conditionId) {
        conditionService.softDeleteCondition(conditionId);
        return ResponseEntity.noContent().build();
    }


}
