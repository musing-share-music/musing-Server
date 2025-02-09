package com.example.musing.mood.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum MoodEnum {
    CALM("편안한"),
    SERENE("차분한"),
    QUIET("조용한"),
    EXCITING("신나는"),
    SAD("슬픈"),
    FAST("빠른"),
    SLOW("느린"),
    LIVELY("경쾌한"),
    TRANQUIL("잔잔한"),
    FOCUS("집중"),
    WORKOUT("운동"),
    HIP("힙한"),
    OLD_FASHIONED("올드한");

    private final String key;

    private static final Map<String, MoodEnum> KEY_TO_ENUM_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(MoodEnum::getKey, Function.identity()));

    public static MoodEnum fromKey(String key) {
        return KEY_TO_ENUM_MAP.get(key);
    }
}
