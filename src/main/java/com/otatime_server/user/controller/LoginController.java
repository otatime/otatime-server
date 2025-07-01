package com.otatime_server.user.controller;

import com.otatime_server.auth.service.JwtService;
import com.otatime_server.global.dto.CommonResponse;
import com.otatime_server.global.dto.TokenResponse;
import com.otatime_server.user.dto.EmailVo;
import com.otatime_server.user.dto.JoinRequest;
import com.otatime_server.user.dto.LoginRequest;
import com.otatime_server.user.dto.UserResponse;
import com.otatime_server.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/join")
    public CommonResponse<UserResponse> join(@RequestBody JoinRequest joinRequest) {
        return new CommonResponse<>(userService.join(joinRequest));
    }

    @PostMapping("/a/login")
    public CommonResponse<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        EmailVo login = userService.login(loginRequest);
        return new CommonResponse<>(jwtService.toTokenResponse(login.email()));
    }
}
