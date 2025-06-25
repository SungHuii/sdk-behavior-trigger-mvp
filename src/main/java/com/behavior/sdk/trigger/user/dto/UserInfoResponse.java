package com.behavior.sdk.trigger.user.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    private UUID userid;
    private String email;
    private Instant createdAt;
}
