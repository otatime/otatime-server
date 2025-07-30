package com.otatime_server.post.domain;

import lombok.Getter;

@Getter
public enum EventType {
    COLLABO_CAFE("콜라보 카페"),
    POPUP_STORE("팝업스토어"),
    EXHIBITION("전시회");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

}
