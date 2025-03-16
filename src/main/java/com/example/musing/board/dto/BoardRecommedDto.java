package com.example.musing.board.dto;

import lombok.Builder;

@Builder
public record BoardRecommedDto(
        int count,
        boolean isLike)
{
    public static BoardRecommedDto of(int count, boolean isLike) {
        return BoardRecommedDto.builder()
                .count(count)
                .isLike(isLike)
                .build();
    }
}
