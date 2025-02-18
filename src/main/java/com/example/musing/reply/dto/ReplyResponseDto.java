package com.example.musing.reply.dto;

import com.example.musing.music.dto.MusicDto;
import com.example.musing.reply.entity.Reply;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReplyResponseDto(
        Long id,
        long starScore,
        String content,
        MusicDto musicDto,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
    public static ReplyResponseDto from(Reply reply) {
        return ReplyResponseDto.builder()
                .id(reply.getId())
                .starScore(reply.getStarScore())
                .content(reply.getContent())
                .musicDto(MusicDto.toDto(reply.getBoard().getMusic()))
                .createdAt(reply.getCreatedAt())
                .updatedAt(reply.getUpdatedAt())
                .build();
    }
}

