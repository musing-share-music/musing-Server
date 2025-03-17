package com.example.musing.board.dto;

import lombok.Builder;

@Builder
public record BoardReplyDto(
        int count,
        float rating)
{
    public static BoardReplyDto of(int count, float rating) {
        return BoardReplyDto.builder()
                .count(count)
                .rating(rating)
                .build();
    }
}
