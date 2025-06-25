package com.behavior.sdk.trigger.user.controller;

import com.behavior.sdk.trigger.user.dto.*;
import com.behavior.sdk.trigger.user.entity.User;
import com.behavior.sdk.trigger.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "User", description = "사용자 관련 API")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/auth/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = userService.signup(request);
        return ResponseEntity.status(201).body(response);
    }

    // 로그인
    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    // 현재 사용자 정보 조회
    @GetMapping("/users/me")
    public ResponseEntity<UserInfoResponse> getMyInfo(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        User user = userService.getCurrentUser(jwt);

        UserInfoResponse response = UserInfoResponse.builder()
                .userid(user.getId())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();

        return ResponseEntity.ok(response);
    }

    // 로그아웃
    @PostMapping("/auth/logout")
    public ResponseEntity<LogoutResponse> logout() {
        return ResponseEntity.ok(new LogoutResponse("로그아웃 성공"));
    }
}
