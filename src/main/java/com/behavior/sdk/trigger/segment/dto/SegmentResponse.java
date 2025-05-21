package com.behavior.sdk.trigger.segment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SegmentResponse {

    private UUID id;
    private UUID projectId;
    private UUID conditionId;
    private List<UUID> visitorIds;
}
