package com.otatime_server.admin.controller;

import com.otatime_server.admin.dto.UserDetail;
import com.otatime_server.admin.dto.UserListResponse;
import com.otatime_server.auth.service.JwtService;
import com.otatime_server.global.dto.CommonResponse;
import com.otatime_server.global.dto.PageInfo;
import com.otatime_server.global.dto.TokenResponse;
import com.otatime_server.post.service.PostService;
import com.otatime_server.user.domain.User;
import com.otatime_server.user.dto.EmailVo;
import com.otatime_server.user.dto.LoginRequest;
import com.otatime_server.user.dto.UserResponse;
import com.otatime_server.user.service.UserLoginService;
import com.otatime_server.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserLoginService userLoginService;
    private final JwtService jwtService;
    private final UserService userService;
    private final PostService postService;

    @PostMapping("/login")
    public CommonResponse<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        EmailVo login = userLoginService.login(loginRequest);
        return new CommonResponse<>(jwtService.toTokenResponse(login.email()));
    }

    @GetMapping("/users")
    public CommonResponse<UserListResponse> getUserInfo() {
        Page<User> result = userService.getUserInfo();
        return new CommonResponse<>(new UserListResponse(
                result.getContent().stream()
                        .map(UserDetail::of)
                        .toList(),
                PageInfo.of(result)
        ));
    }

    @DeleteMapping("/users/{userId}")
    public CommonResponse<UserResponse> deleteUser(@PathVariable Long userId) {
        Long deletedUserId = userService.deleteUser(userId);
        return new CommonResponse<>(new UserResponse(deletedUserId));
    }

}
