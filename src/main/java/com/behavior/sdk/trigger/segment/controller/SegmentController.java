package com.behavior.sdk.trigger.segment.controller;

import com.behavior.sdk.trigger.segment.dto.SegmentCreateRequest;
import com.behavior.sdk.trigger.segment.dto.SegmentResponse;
import com.behavior.sdk.trigger.segment.entity.Segment;
import com.behavior.sdk.trigger.segment.entity.SegmentVisitor;
import com.behavior.sdk.trigger.segment.service.SegmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/segments")
@RequiredArgsConstructor
@Tag(name = "Segment", description = "세그먼트 관리 API")
public class SegmentController {

    private final SegmentService segmentService;

    @PostMapping
    @Operation(summary = "세그먼트 생성", description = "세그먼트를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "세그먼트 생성 성공")
    public ResponseEntity<SegmentResponse> createSegment(@RequestBody SegmentCreateRequest createRequest) {

        Segment segment = segmentService.createSegment(createRequest);

        List<UUID> visitorIds = segment.getSegmentVisitors().stream()
                .map(SegmentVisitor::getVisitorId)
                .collect(Collectors.toList());

        SegmentResponse segmentResponse = new SegmentResponse(
                segment.getId(),
                segment.getProjectId(),
                segment.getConditionId(),
                visitorIds,
                segment.getDeletedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(segmentResponse);
    }

    @GetMapping
    @Operation(summary = "프로젝트별 모든 세그먼트 조회", description = "프로젝트 ID로 모든 세그먼트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "세그먼트 목록 조회 성공")
    public ResponseEntity<List<SegmentResponse>> listResponseEntity(@RequestParam("projectId") UUID projectId) {
        List<SegmentResponse> segments = segmentService.listSegments(projectId);
        return ResponseEntity.ok(segments);
    }

    @GetMapping("/{segmentId}")
    @Operation(summary = "단일 세그먼트 조회", description = "단일 세그먼트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "세그먼트 조회 성공")
    public ResponseEntity<SegmentResponse> getSegment(@PathVariable UUID segmentId) {
        SegmentResponse segment = segmentService.getSegment(segmentId);
        return ResponseEntity.ok(segment);
    }

    @DeleteMapping("/{segmentId}")
    @Operation(summary = "세그먼트를 soft delete 합니다.", description = "세그먼트를 soft delete 합니다.")
    @ApiResponse(responseCode = "204", description = "세그먼트 삭제 성공")
    public ResponseEntity<Void> deleteSegment(@PathVariable UUID segmentId) {
        segmentService.deleteSegment(segmentId);
        return ResponseEntity.noContent().build();
    }
}
