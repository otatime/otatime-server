package com.otatime_server.post.domain;

public enum Category {

    GAME("게임"),
    ANIMATION("애니메이션"),
    EVENT("행사");

    private final String value;

    Category(String value) {
        this.value = value;
    }

}
