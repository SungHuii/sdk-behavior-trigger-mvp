package com.behavior.sdk.trigger.segment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "EmailBatchResponse", description = "이메일 배치 응답 payload")
public class EmailBatchResponse {

    @Schema(description = "이메일 배치 ID", example = "3f8b1c2e-4d3a-4b5e-8c7f-9a0d1e2f3a4b")
    private UUID batchId;

    @Schema(description = "보낸 이메일 수", example = "100")
    private int sentCount;

    @Schema(description = "실패한 이메일 수", example = "5")
    private int failedCount;
}
