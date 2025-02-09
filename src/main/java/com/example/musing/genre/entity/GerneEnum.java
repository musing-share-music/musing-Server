package com.example.musing.genre.entity;

import com.example.musing.mood.entity.MoodEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum GerneEnum {
    KPOP("K-POP"),
    JPOP("J-POP"),
    CLASSIC("클래식"),
    BALLADE("발라드"),
    ALTERNATIVE("얼터너티브"),
    INDIE("인디"),
    DISCO("디스코"),
    ROCK("록"),
    METAL("메탈"),
    SYNTHPOP("신디팝"),
    RANDB("R&B"),
    NEWWAVE("뉴웨이브"),
    FOLK("포크"),
    COUNTRY("컨트리"),
    BLUES("블루스"),
    ELECTRONIC("일렉트로닉"),
    TROT("트로트"),
    OST("OST"),
    CCM("CCM"),
    MUSICAL("뮤지컬"),
    EDM("EDM"),
    SHOEGAZING("슈게이징"),
    Unknown("알수없음");


    private final String key;

    private static final Map<String, GerneEnum> KEY_TO_ENUM_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(GerneEnum::getKey, Function.identity()));

    public static GerneEnum fromKey(String key) {
        return KEY_TO_ENUM_MAP.get(key);
    }
}