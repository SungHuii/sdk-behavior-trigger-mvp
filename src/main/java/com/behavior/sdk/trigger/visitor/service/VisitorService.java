package com.behavior.sdk.trigger.visitor.service;

import com.behavior.sdk.trigger.visitor.dto.VisitorResponse;

import java.util.UUID;

public interface VisitorService {

   VisitorResponse createVisitor(UUID projectId);
   boolean existsVisitor(UUID visitorId);


}
