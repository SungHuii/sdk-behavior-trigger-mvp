package com.behavior.sdk.trigger.email.messaging.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmailSendMessage {

    private UUID projectId;
    private UUID visitorId;     // 로그 & 추적 연계
    private String to;
    private String subject;
    private String body;        // 현재 하드코딩 형식
    private UUID templateId; // 추후 템플릿 관리 기능 추가시 활용
    private Map<String, String> vars; // 템플릿 변수 치환용
    private LocalDateTime requestedAt;
    private String dedupKey;    // 멱등키 : projectId:visitorId:subject 형태


}
