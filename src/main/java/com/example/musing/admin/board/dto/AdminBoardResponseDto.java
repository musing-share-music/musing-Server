package com.example.musing.admin.board.dto;

import com.example.musing.board.entity.Board;
import lombok.Builder;

import java.time.LocalDateTime;

public class AdminBoardResponseDto {
    @Builder
    public record AdminBoardListDto(
            long id,
            String title,
            String username,
            LocalDateTime createdAt
    ) {
        public static AdminBoardListDto toDto(Board board) {
            return AdminBoardListDto.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .username(board.getUser().getUsername())
                    .createdAt(board.getCreatedAt())
                    .build();
        }
    }
}
