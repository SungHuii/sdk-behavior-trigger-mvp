package com.behavior.sdk.trigger.segment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
@Builder
public class SegmentCreateRequest {

    private UUID projectId;
    private UUID conditionId;
}
