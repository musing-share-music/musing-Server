package com.example.musing.reply.dto;

import com.example.musing.board.dto.BoardReplyDto;
import com.example.musing.music.dto.MusicDto;
import com.example.musing.reply.entity.Reply;
import com.example.musing.user.dto.UserResponseDto;
import lombok.Builder;

import java.time.LocalDateTime;

public class ReplyResponseDto {

    @Builder
    public record ReplyAndUpdatedBoardDto(
            BoardReplyDto boardReplyDto,
            ReplyDto replyDto
    ) {
        public static ReplyAndUpdatedBoardDto of(int count, float rating, Reply reply){
            return ReplyAndUpdatedBoardDto.builder()
                    .boardReplyDto(BoardReplyDto.of(count, rating))
                    .replyDto(ReplyDto.from(reply))
                    .build();
        }
    }

    @Builder
    public record ReplyDto(
            Long id,
            long starScore,
            String content,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            UserResponseDto.UserProfileDto profileInfo

    ) {
        public static ReplyResponseDto.ReplyDto from(Reply reply) {
            return ReplyResponseDto.ReplyDto.builder()
                    .id(reply.getId())
                    .starScore(reply.getStarScore())
                    .content(reply.getContent())
                    .createdAt(reply.getCreatedAt())
                    .updatedAt(reply.getUpdatedAt())
                    .profileInfo(UserResponseDto.UserProfileDto.of(reply.getUser()))
                    .build();
        }
    }

    @Builder
    public record MyReplyDto(
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
        public static ReplyResponseDto.MyReplyDto from(Reply reply) {
            return ReplyResponseDto.MyReplyDto.builder()
                    .id(reply.getId())
                    .starScore(reply.getStarScore())
                    .content(reply.getContent())
                    .musicDto(MusicDto.toDto(reply.getBoard().getMusic()))
                    .createdAt(reply.getCreatedAt())
                    .updatedAt(reply.getUpdatedAt())
                    .build();
        }
    }
}
