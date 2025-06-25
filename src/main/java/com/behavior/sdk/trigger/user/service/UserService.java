package com.behavior.sdk.trigger.user.service;

import com.behavior.sdk.trigger.user.dto.LoginRequest;
import com.behavior.sdk.trigger.user.dto.LoginResponse;
import com.behavior.sdk.trigger.user.dto.SignupRequest;
import com.behavior.sdk.trigger.user.dto.SignupResponse;
import com.behavior.sdk.trigger.user.entity.User;

public interface UserService {

    SignupResponse signup(SignupRequest request);

    LoginResponse login(LoginRequest request);

    User getCurrentUser(String token);
}
