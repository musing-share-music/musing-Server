package com.example.musing.reply.dto;

import com.example.musing.music.dto.MusicDto;
import com.example.musing.reply.entity.Reply;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReplyResponseDto(
        @Schema(description = "리뷰의 Id")
        Long id,
        @Schema(description = "리뷰의 별점", example = "5.0")
        long starScore,
        @Schema(description = "리뷰의 내용", example = "신나서 운동하면서 듣기 좋아요")
        String content,
        @Schema(description = "작성한 리뷰의 음악 정보")
        MusicDto musicDto,
        @Schema(description = "작성일자")
        LocalDateTime createdAt,
        @Schema(description = "수정일자")
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

