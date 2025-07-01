package com.otatime_server.user.controller;

import com.otatime_server.auth.service.JwtService;
import com.otatime_server.global.dto.CommonResponse;
import com.otatime_server.global.dto.TokenResponse;
import com.otatime_server.user.dto.EmailVo;
import com.otatime_server.user.dto.JoinRequest;
import com.otatime_server.user.dto.LoginRequest;
import com.otatime_server.user.dto.UserResponse;
import com.otatime_server.user.service.UserLoginService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final UserLoginService userLoginService;
    private final JwtService jwtService;

    @PostMapping("/join")
    public CommonResponse<UserResponse> join(@RequestBody JoinRequest joinRequest) {
        return new CommonResponse<>(userLoginService.join(joinRequest));
    }

    @PostMapping("/login")
    public CommonResponse<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        EmailVo login = userLoginService.login(loginRequest);
        return new CommonResponse<>(jwtService.toTokenResponse(login.email()));
    }

    @PostMapping("/re-issue")
    public CommonResponse<TokenResponse> reIssueToken(HttpServletRequest request) {
        String token = getRefreshToken(request);
        EmailVo emailByToken = userLoginService.findEmailByToken(token);
        return new CommonResponse<>(jwtService.toTokenResponse(emailByToken.email()));
    }

    private String getRefreshToken(HttpServletRequest request) {
        String authentication = request.getHeader("Authentication");
        authentication = authentication.substring("Bearer".length()).trim();
        return authentication;
    }
}
