package com.behavior.sdk.trigger.visitor.controller;

import com.behavior.sdk.trigger.visitor.dto.VisitorResponse;
import com.behavior.sdk.trigger.visitor.service.VisitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
   public ResponseEntity<VisitorResponse> create(@Parameter(description = "해당 프로젝트 키") @RequestParam UUID projectId) {
      VisitorResponse response = visitorService.createVisitor(projectId);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }

   @GetMapping("/exists/{visitorId}")
   @Operation(summary = "방문자 존재 여부 확인", description = "방문자가 존재하는지 확인합니다.")
   @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "방문자 존재 여부 확인 성공"),
        @ApiResponse(responseCode = "404", description = "방문자를 찾을 수 없음")
   })
   public ResponseEntity<Boolean> exists(@Parameter(description = "방문자 ID") @PathVariable UUID visitorId) {
      boolean exists = visitorService.existsVisitor(visitorId);
      return ResponseEntity.ok(exists);
   }

   @PostMapping("/{visitorId}/email")
   @Operation(summary = "방문자 이메일 업데이트", description = "방문자의 이메일을 업데이트합니다.")
    @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "방문자 이메일 업데이트 성공"),
          @ApiResponse(responseCode = "400", description = "잘못된 요청"),
          @ApiResponse(responseCode = "404", description = "방문자를 찾을 수 없음")
    })
   public ResponseEntity<Void> updateEmail(
           @Parameter(description = "방문자 ID") @PathVariable UUID visitorId,
           @Parameter(description = "이메일") @Valid @RequestParam String email) {

      visitorService.updateEmail(visitorId, email);
      return ResponseEntity.ok().build();
   }
}
