package com.otatime_server.post.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum EventType {
    COLLABO_CAFE("콜라보 카페"),
    POPUP_STORE("팝업스토어"),
    EXHIBITION("전시회");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static EventType fromValue(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(EventType.values())
                .filter(e -> e.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 이벤트 유형 값: " + value));
    }
}
