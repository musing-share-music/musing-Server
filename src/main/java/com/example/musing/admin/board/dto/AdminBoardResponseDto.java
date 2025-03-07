package com.example.musing.admin.board.dto;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.board.entity.Board;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.mood.dto.MoodDto;
import com.example.musing.user.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;

public class AdminBoardResponseDto {
    @Builder
    public record BoardListDto(
            String title,
            String username,
            LocalDateTime createdAt
    ) {
        public static BoardListDto toDto(Board board) {
            return BoardListDto.builder()
                    .title(board.getTitle())
                    .username(board.getUser().getUsername())
                    .createdAt(board.getCreatedAt())
                    .build();
        }
    }
}
