package com.behavior.sdk.trigger.visitor.controller;

import com.behavior.sdk.trigger.visitor.dto.VisitorResponse;
import com.behavior.sdk.trigger.visitor.service.VisitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/visitors")
@RequiredArgsConstructor
@Tag(name = "Visitor", description = "방문자 관리 API")
public class VisitorController {

   private final VisitorService visitorService;

   @PostMapping
   @Operation(summary = "방문자 생성", description = "프로젝트 키로 방문자를 생성합니다.")
   @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "방문자 생성 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청"),
      @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
   })
   public ResponseEntity<VisitorResponse> create(@Parameter(description = "해당 프로젝트 키") @RequestParam String projectKey) {
      VisitorResponse response = visitorService.createVisitor(projectKey);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }
}
