package com.otatime_server.user.controller;

import com.otatime_server.global.dto.CommonResponse;
import com.otatime_server.user.dto.MyPageResponse;
import com.otatime_server.user.dto.UpdateProfileImageVO;
import com.otatime_server.user.dto.UpdateUsernameVO;
import com.otatime_server.user.dto.UserResponse;
import com.otatime_server.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/me")
public class UserController {

    private final UserService userService;

    @GetMapping
    public CommonResponse<MyPageResponse> getMyPage() {
        String loginUserEmail = getLoginUserEmail();
        MyPageResponse myPage = userService.getMyPage(loginUserEmail);
        return new CommonResponse<>(myPage);
    }

    @PatchMapping("/username")
    public CommonResponse<UserResponse> changeUsername(@RequestBody UpdateUsernameVO username) {
        return new CommonResponse<>(userService.updateUsername(getLoginUserEmail(), username));
    }

    @PatchMapping("/image")
    public CommonResponse<UserResponse> changeImage(@RequestBody UpdateProfileImageVO profileImage) {
        return new CommonResponse<>(userService.updateProfileImage(getLoginUserEmail(), profileImage));
    }

    private String getLoginUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
