package com.example.musing.reply.dto;

import com.example.musing.reply.entity.Reply;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReplyRequestDto(
        long starScore,
        String content

) {
    public static ReplyRequestDto from(Reply reply) {
        return ReplyRequestDto.builder()
                .starScore(reply.getStarScore())
                .content(reply.getContent())
                .build();
    }
}
