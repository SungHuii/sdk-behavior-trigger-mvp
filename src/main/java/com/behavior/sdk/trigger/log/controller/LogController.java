package com.behavior.sdk.trigger.log.controller;

import com.behavior.sdk.trigger.log.dto.LogCreateRequest;
import com.behavior.sdk.trigger.log.dto.LogResponse;
import com.behavior.sdk.trigger.log.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import java.util.List;

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

   @GetMapping
   @Operation(summary = "로그 목록 조회", description = "projectKey, visitorKey로 로그를 필터링 조회합니다. 둘 다 없으면 전체 로그")
   @ApiResponses({
         @ApiResponse(responseCode = "200", description = "로그 목록 조회 성공",
               content=@Content(
                     mediaType = "application/json",
                     array = @ArraySchema(schema = @Schema(implementation = LogResponse.class)))),
         @ApiResponse(responseCode = "400", description = "잘못된 요청",
               content = @Content),  // content 생략하면 예시 없이 빈 바디로 표시
         @ApiResponse(responseCode = "404", description = "프로젝트/방문자 찾을 수 없음",
               content = @Content)
   })
   public ResponseEntity<List<LogResponse>> listLogs(
         @Parameter(description = "프로젝트 키") @RequestParam(required = false) String projectKey,
         @Parameter(description = "방문자 키") @RequestParam(required = false) String visitorKey) {

      List<LogResponse> logs = logService.findLogs(projectKey, visitorKey);
      return ResponseEntity.ok(logs);
   }

   @GetMapping("/{visitorKey}")
   @Operation(summary = "방문자 키로 로그 조회", description = "방문자 키로 로그를 조회합니다.")
   @ApiResponses({
         @ApiResponse(responseCode = "200", description = "로그 조회 성공",
               content=@Content(schema = @Schema(implementation = LogResponse.class))),
         @ApiResponse(responseCode = "404", description = "방문자 키에 해당하는 로그가 없음")
   })
   public ResponseEntity<List<LogResponse>> getLogsByVisitorKey(
         @Parameter(description = "방문자 키") @PathVariable String visitorKey) {

      List<LogResponse> logs = logService.findLogs(null, visitorKey);
      return ResponseEntity.ok(logs);
   }
}
