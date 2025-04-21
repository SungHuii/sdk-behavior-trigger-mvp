package com.behavior.sdk.trigger.visitor.controller;

import com.behavior.sdk.trigger.visitor.dto.VisitorResponse;
import com.behavior.sdk.trigger.visitor.service.VisitorService;
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
public class VisitorController {

   private final VisitorService visitorService;

   @PostMapping
   public ResponseEntity<VisitorResponse> create(@RequestParam String projectKey) {
      VisitorResponse response = visitorService.createVisitor(projectKey);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }
}
