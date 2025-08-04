package com.otatime_server.post.controller;

import com.otatime_server.global.dto.CommonResponse;
import com.otatime_server.global.dto.PageInfo;
import com.otatime_server.post.dto.DailyPostsRequest;
import com.otatime_server.post.dto.MonthlyPostsRequest;
import com.otatime_server.post.dto.PostDetail;
import com.otatime_server.post.dto.PostListResponse;
import com.otatime_server.post.dto.PostResponse;
import com.otatime_server.post.dto.PostSearchRequest;
import com.otatime_server.post.dto.ReportRequest;
import com.otatime_server.post.dto.SearchPostsRequest;
import com.otatime_server.post.service.PostService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping("/reports")
    public CommonResponse<PostResponse> report(@RequestBody ReportRequest reportRequest) {
        return new CommonResponse<>(postService.report(getLoginUserEmail(), reportRequest));
    }

    @PatchMapping("/like/{postId}")
    public CommonResponse<PostResponse> like(@PathVariable Long postId) {
        return new CommonResponse<>(postService.likePost(getLoginUserEmail(), postId));
    }

    @GetMapping
    public CommonResponse<PostListResponse> getPosts(
            Pageable pageable,
            @Validated PostSearchRequest request
    ) {
        Page<PostDetail> mainPage = postService.getMainPage(
                pageable,
                request.start(),
                request.end(),
                request.region(),
                getLoginUserEmail(),
                request.categories(),
                request.eventTypes()
        );

        return new CommonResponse<>(new PostListResponse(
                mainPage.getContent(),
                PageInfo.of(mainPage)
        ));
    }

    @GetMapping("/{postId}")
    public CommonResponse<PostDetail> getPost(@PathVariable Long postId) {
        return new CommonResponse<>(postService.getPostDetail(postId, getLoginUserEmail()));
    }

    @GetMapping("/date")
    public CommonResponse<PostListResponse> getDailyPosts(
            Pageable pageable,
            @Validated DailyPostsRequest request
    ) {
        Page<PostDetail> datePage = postService.getDatePage(pageable, request.date(), getLoginUserEmail());
        return new CommonResponse<>(new PostListResponse(
                datePage.getContent(),
                PageInfo.of(datePage)
        ));
    }

    @GetMapping("/month")
    public CommonResponse<PostListResponse> getMonthlyPosts(
            Pageable pageable,
            @Validated MonthlyPostsRequest request
    ) {
        Page<PostDetail> page = postService.getMonthPage(pageable, request.month(), getLoginUserEmail());
        return new CommonResponse<>(new PostListResponse(
                page.getContent(),
                PageInfo.of(page)
        ));
    }

    @GetMapping("/banner")
    public CommonResponse<PostListResponse> getBanner() {
        List<PostDetail> banner = postService.getBanner();
        return new CommonResponse<>(new PostListResponse(
                banner,
                null
        ));
    }

    @GetMapping("/search")
    public CommonResponse<PostListResponse> getSearchPosts(
            Pageable pageable,
            @Validated SearchPostsRequest request
    ) {
        Page<PostDetail> search = postService.search(request.keyword(), pageable, getLoginUserEmail());
        return new CommonResponse<>(new PostListResponse(
                search.getContent(),
                PageInfo.of(search)
        ));
    }

    private String getLoginUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
