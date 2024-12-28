package com.example.musing.board.dto;

import com.example.musing.board.entity.Board;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record BoardDto(
        @Schema(description = "음악 추천 게시판 고유 ID", example = "1")
        long id,
        @Schema(description = "음악 추천 게시판 제목", example = "진짜 이노래는 꼭 들어봐야함.")
        String title,
        @Schema(description = "노래 제목", example = "Layla (Live at Royal Albert Hall, 1991) (Orchestral Version)")
        String musicName,
        @Schema(description = "아티스트 명", example = "Eric Clapton")
        String artist,
        @Schema(description = "유튜브 썸네일 사진 링크", example = "https://img.youtube.com/vi/-KG2O5PSCSs/maxresdefault.jpg")
        String thumbNailLink) { //메인 페이지 음악 추천 게시판 인기곡 부분과 좋아요한 음악
    public static BoardDto toDto(Board board) {
        return BoardDto.builder()
                .title(board.getTitle())
                .musicName(board.getMusic().getName())
                .artist(board.getMusic().getArtist())
                .thumbNailLink(board.getMusic().getThumbNailLink())
                .build();
    }
}
