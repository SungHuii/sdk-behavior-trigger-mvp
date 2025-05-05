package com.behavior.sdk.trigger.log_event.service;

import com.behavior.sdk.trigger.log_event.dto.LogEventCreateRequest;
import com.behavior.sdk.trigger.log_event.dto.LogEventResponse;

import java.util.List;
import java.util.UUID;

public interface LogEventService {
   LogEventResponse createLogEvent(UUID projectId, UUID visitorId, LogEventCreateRequest request);

   List<LogEventResponse> findLogEvents(UUID projectId, UUID visitorId);

   void softDeleteLogEvent(UUID logEventId);


}
