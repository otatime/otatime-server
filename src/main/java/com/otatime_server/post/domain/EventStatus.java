package com.otatime_server.post.domain;

import lombok.Getter;

@Getter
public enum EventStatus {
    SCHEDULED("진행 예정"),
    IN_PROGRESS("진행중"),
    COMPLETED("종료");

    private final String value;

    EventStatus(String value) {
        this.value = value;
    }

}
