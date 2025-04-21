package com.behavior.sdk.trigger.log.controller;

import com.behavior.sdk.trigger.log.dto.LogCreateRequest;
import com.behavior.sdk.trigger.log.dto.LogResponse;
import com.behavior.sdk.trigger.log.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

   private final LogService logService;

   @PostMapping
   public ResponseEntity<LogResponse> createLog(@RequestBody LogCreateRequest logRequest) {
      LogResponse createdLog = logService.createLog(logRequest);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdLog);
   }
}
