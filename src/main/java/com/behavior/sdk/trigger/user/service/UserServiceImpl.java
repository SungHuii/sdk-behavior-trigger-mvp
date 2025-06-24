package com.behavior.sdk.trigger.user.service;

import com.behavior.sdk.trigger.user.dto.LoginRequest;
import com.behavior.sdk.trigger.user.dto.LoginResponse;
import com.behavior.sdk.trigger.user.dto.SignupRequest;
import com.behavior.sdk.trigger.user.dto.SignupResponse;
import com.behavior.sdk.trigger.user.entity.User;
import com.behavior.sdk.trigger.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;


    @Override
    public SignupResponse signup(SignupRequest request) {
        return null;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        return null;
    }

    @Override
    public User getCurrentUser(String token) {
        return null;
    }
}
