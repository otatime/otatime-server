package com.otatime_server.admin.dto;

import com.otatime_server.global.dto.PageInfo;
import java.util.List;

public record UserListResponse(
        List<UserDetail> users,
        PageInfo meta
) {
}
