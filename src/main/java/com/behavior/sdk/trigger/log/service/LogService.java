package com.behavior.sdk.trigger.log.service;

import com.behavior.sdk.trigger.log.dto.LogCreateRequest;
import com.behavior.sdk.trigger.log.dto.LogResponse;

import java.util.List;

public interface LogService {
   LogResponse createLog(String projectKey, String visitorKey, LogCreateRequest request);

   List<LogResponse> findLogs(String projectKey, String visitorKey);


}
