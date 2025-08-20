package com.otatime_server.admin.controller;

import com.otatime_server.admin.dto.UserDetail;
import com.otatime_server.admin.dto.UserListResponse;
import com.otatime_server.auth.service.JwtService;
import com.otatime_server.global.dto.CommonResponse;
import com.otatime_server.global.dto.PageInfo;
import com.otatime_server.global.dto.TokenResponse;
import com.otatime_server.post.domain.Post;
import com.otatime_server.post.domain.PostStatus;
import com.otatime_server.post.dto.PostDetail;
import com.otatime_server.post.dto.PostListResponse;
import com.otatime_server.post.dto.PostRequest;
import com.otatime_server.post.dto.PostResponse;
import com.otatime_server.post.dto.PostUpdateRequest;
import com.otatime_server.post.service.PostService;
import com.otatime_server.user.domain.User;
import com.otatime_server.user.dto.EmailVo;
import com.otatime_server.user.dto.LoginRequest;
import com.otatime_server.user.dto.UserResponse;
import com.otatime_server.user.service.UserLoginService;
import com.otatime_server.user.service.UserService;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @GetMapping("/posts")
    public CommonResponse<PostListResponse> getPostedList() {
        Page<Post> result = postService.getPostList(PostStatus.PUBLISHED);
        return new CommonResponse<>(new PostListResponse(
                result.getContent().stream()
                        .map(it -> PostDetail.of(it, Collections.emptyList()))
                        .toList(),
                PageInfo.of(result)
        ));
    }

    @GetMapping("/report")
    public CommonResponse<PostListResponse> getReportList() {
        Page<Post> result = postService.getPostList(PostStatus.PENDING);
        return new CommonResponse<>(new PostListResponse(
                result.getContent().stream()
                        .map(it -> PostDetail.of(it, Collections.emptyList()))
                        .toList(),
                PageInfo.of(result)
        ));
    }

    @GetMapping("/posts/{postId}")
    public CommonResponse<PostDetail> getPostDetail(@PathVariable Long postId) {
        return new CommonResponse<>(postService.getPostDetail(postId, ""));
    }

    @DeleteMapping("/posts/{postId}")
    public CommonResponse<PostResponse> deletePost(@PathVariable Long postId) {
        return new CommonResponse<>(new PostResponse(postService.deletePost(postId)));
    }

    @PatchMapping("/report/{postId}")
    public CommonResponse<PostResponse> updatePost(@PathVariable Long postId) {
        Long updatedPostId = postService.updateToPost(postId);
        return new CommonResponse<>(new PostResponse(updatedPostId));
    }

    @PostMapping("/posts")
    public CommonResponse<PostResponse> post(@RequestBody PostRequest postRequest) {
        PostResponse result = postService.post(getLoginUserEmail(), postRequest);
        return new CommonResponse<>(result);
    }

    @PatchMapping("/posts/{postId}")
    public CommonResponse<PostResponse> updatePost(@PathVariable Long postId, @RequestBody PostUpdateRequest postUpdateRequest) {
        PostResponse result = postService.updatePost(postId, postUpdateRequest);
        return new CommonResponse<>(result);
    }

    private String getLoginUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
