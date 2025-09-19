package com.behavior.sdk.trigger.common.security;

import com.behavior.sdk.trigger.common.exception.ErrorSpec;
import com.behavior.sdk.trigger.common.exception.FieldErrorDetail;
import com.behavior.sdk.trigger.common.exception.ServiceException;
import com.behavior.sdk.trigger.user.entity.User;
import com.behavior.sdk.trigger.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // 토큰이 없으면 익명 통과
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰이 있으면 반드시 유효해야 함 (만료/위조 시 예외 발생 -> 401)
        String token = authHeader.replace("Bearer ", "").trim();

        // JwtUtils 내부에서 만료/위조 시 ServiceException(AUTH_EXPIRED_TOKEN, AUTH_INVALID_TOKEN) 발생
        jwtUtils.validateTokenOrThrow(token);

        // 토큰에서 사용자 식별 후 로드
        UUID userId = jwtUtils.getUserIdFromToken(token); // 실패 시 예외
        
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            // 사용자 존재 노출 방지: '인증 실패'로 통일
            throw new ServiceException(
                ErrorSpec.AUTH_INVALID_CREDENTIALS,
                "이메일/비밀번호가 올바르지 않습니다.",
                Collections.singletonList(new FieldErrorDetail("userId","not found", userId))
            );
        }

        User user = userOptional.get();

        // 인증 객체 구성
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 체인 계속
        filterChain.doFilter(request, response);

    }
}
