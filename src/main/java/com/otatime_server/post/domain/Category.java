package com.otatime_server.post.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

public enum Category {

    GAME("게임"),
    ANIMATION("애니메이션"),
    EVENT("행사");

    private final String value;

    Category(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Category fromValue(String value) {
        return Arrays.stream(Category.values())
                .filter(c -> c.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 카테고리 값: " + value));
    }
}
