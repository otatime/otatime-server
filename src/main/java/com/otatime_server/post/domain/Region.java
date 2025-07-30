package com.otatime_server.post.domain;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Region {

    SEOUL("서울"),
    GYEONGGI("경기"),
    INCHEON("인천"),
    GANGWON("강원"),
    DAEGU("대구"),
    DAEJEON("대전"),
    CHUNGBUK("충북"),
    CHUNGNAM("충남"),
    SEJONG("세종"),
    GYEONGBUK("경북"),
    GYEONGNAM("경남"),
    ULSAN("울산"),
    BUSAN("부산"),
    JEONBUK("전북"),
    JEONNAM("전남"),
    GWANGJU("광주");

    private final String value;

    Region(String value) {
        this.value = value;
    }

    public static Region getRegionByValue(String value) {
        return Arrays.stream(Region.values())
                .filter(it -> it.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지역입니다."));
    }

}
