package com.example.musing.reply.dto;

import com.example.musing.reply.entity.Reply;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReplyRequestDto(
        Long id,
        long starScore,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
    public static ReplyRequestDto from(Reply reply) {
        return ReplyRequestDto.builder()
                .id(reply.getId())
                .starScore(reply.getStarScore())
                .content(reply.getContent())
                .createdAt(reply.getCreatedAt())
                .updatedAt(reply.getUpdatedAt())
                .build();
    }
}
