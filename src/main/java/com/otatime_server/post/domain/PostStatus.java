package com.otatime_server.post.domain;

import lombok.Getter;

@Getter
public enum PostStatus {
    PUBLISHED("게시중"),
    PENDING("대기중"),
    DELETED("삭제됨");

    private final String value;

    PostStatus(String value) {
        this.value = value;
    }

}
