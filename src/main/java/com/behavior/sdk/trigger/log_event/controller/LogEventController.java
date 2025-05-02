package com.behavior.sdk.trigger.log_event.controller;

import com.behavior.sdk.trigger.log_event.dto.LogEventCreateRequest;
import com.behavior.sdk.trigger.log_event.dto.LogEventResponse;
import com.behavior.sdk.trigger.log_event.service.LogEventService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Tag(name = "Log", description = "로그 관리 API")
public class LogEventController {

   private final LogEventService logEventService;

   @PostMapping
   @Operation(summary = "로그 생성", description = "프로젝트 키, 방문자 키로 로그를 생성합니다.")
   @ApiResponses({
         @ApiResponse(responseCode = "201", description = "로그 생성 성공",
               content=@Content(schema = @Schema(implementation = LogEventResponse.class))),
   })
   public ResponseEntity<LogEventResponse> createLog(
         @Parameter(description = "프로젝트 키") @RequestParam UUID projectId,
         @Parameter(description = "방문자 키") @RequestParam UUID visitorId,
         @Parameter(description = "요청 바디") @RequestBody @Valid LogEventCreateRequest logRequest) {

      LogEventResponse createdLogEvent = logEventService.createLogEvent(projectId, visitorId, logRequest);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdLogEvent);
   }

   @GetMapping
   @Operation(summary = "로그 목록 조회", description = "projectId, visitorId로 로그를 필터링 조회합니다. 둘 다 없으면 전체 로그")
   @ApiResponses({
         @ApiResponse(responseCode = "200", description = "로그 목록 조회 성공",
               content=@Content(
                     mediaType = "application/json",
                     array = @ArraySchema(schema = @Schema(implementation = LogEventResponse.class)))),
         @ApiResponse(responseCode = "400", description = "잘못된 요청",
               content = @Content),  // content 생략하면 예시 없이 빈 바디로 표시
         @ApiResponse(responseCode = "404", description = "프로젝트/방문자 찾을 수 없음",
               content = @Content)
   })
   public ResponseEntity<List<LogEventResponse>> listLogs(
         @Parameter(description = "프로젝트 키") @RequestParam UUID projectId,
         @Parameter(description = "방문자 키") @RequestParam(required = false) UUID visitorId) {

      List<LogEventResponse> logs = logEventService.findLogEvents(projectId, visitorId);
      return ResponseEntity.ok(logs);
   }

   @DeleteMapping("/{logEventId}")
   @Operation(summary = "로그 삭제", description = "로그를 soft delete 합니다.")
   @ApiResponses({
         @ApiResponse(responseCode = "204", description = "로그 삭제 성공"),
         @ApiResponse(responseCode = "404", description = "로그 찾을 수 없음",
               content = @Content)
   })
   public ResponseEntity<Void> deleteLog(
         @Parameter(description = "로그 ID") @PathVariable UUID logEventId) {

      logEventService.softDeleteLogEvent(logEventId);
      return ResponseEntity.noContent().build();
   }
}
