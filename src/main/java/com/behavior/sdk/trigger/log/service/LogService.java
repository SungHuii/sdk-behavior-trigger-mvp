package com.behavior.sdk.trigger.log.service;

import com.behavior.sdk.trigger.log.dto.LogCreateRequest;
import com.behavior.sdk.trigger.log.dto.LogResponse;

public interface LogService {
   LogResponse createLog(LogCreateRequest request);
}
