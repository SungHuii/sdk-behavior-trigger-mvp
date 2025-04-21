package com.behavior.sdk.trigger.log.controller;

import com.behavior.sdk.trigger.log.dto.LogCreateRequest;
import com.behavior.sdk.trigger.log.dto.LogResponse;
import com.behavior.sdk.trigger.log.service.LogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

   private final LogService logService;

   @PostMapping
   public ResponseEntity<LogResponse> createLog(
         @RequestParam String projectKey, @RequestParam String visitorKey, @RequestBody @Valid LogCreateRequest logRequest) {

      LogResponse createdLog = logService.createLog(projectKey, visitorKey, logRequest);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdLog);
   }
}
