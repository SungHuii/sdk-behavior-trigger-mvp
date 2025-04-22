package com.behavior.sdk.trigger.log.controller;

import com.behavior.sdk.trigger.log.dto.LogCreateRequest;
import com.behavior.sdk.trigger.log.dto.LogResponse;
import com.behavior.sdk.trigger.log.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Tag(name = "Log", description = "로그 관리 API")
public class LogController {

   private final LogService logService;

   @PostMapping
   @Operation(summary = "로그 생성", description = "프로젝트 키, 방문자 키로 로그를 생성합니다.")
   @ApiResponses({
         @ApiResponse(responseCode = "201", description = "로그 생성 성공",
               content=@Content(schema = @Schema(implementation = LogResponse.class))),
   })
   public ResponseEntity<LogResponse> createLog(
         @Parameter(description = "프로젝트 키") @RequestParam String projectKey,
         @Parameter(description = "방문자 키") @RequestParam String visitorKey,
         @Parameter(description = "요청 바디") @RequestBody @Valid LogCreateRequest logRequest) {

      LogResponse createdLog = logService.createLog(projectKey, visitorKey, logRequest);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdLog);
   }
}
