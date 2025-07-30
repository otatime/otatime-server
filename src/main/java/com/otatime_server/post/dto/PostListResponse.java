package com.otatime_server.post.dto;

import com.otatime_server.global.dto.PageInfo;
import java.util.List;

public record PostListResponse(
        List<PostDetail> result,
        PageInfo meta
) {
}
