package com.example.musing.board.dto;

import com.example.musing.board.entity.Board;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record GenreBoardDto(
        @Schema(description = "장르관련 음악 추천 게시판 고유 ID", example = "21")
        long id,
        @Schema(description = "노래 제목", example = "Back In Black")
        String musicName,
        @Schema(description = "아티스트 명", example = "AC/DC")
        String artist,
        @Schema(description = "유튜브 썸네일 사진 링크", example = "https://img.youtube.com/vi/pAgnJDJN4VA/maxresdefault.jpg")
        String thumbNailLink) { //메인 페이지 장르 추천에 쓰임
    public static GenreBoardDto toDto(Board board){
        return GenreBoardDto.builder()
                .id(board.getId())
                .musicName(board.getMusic().getName())
                .artist(board.getMusic().getArtist())
                .thumbNailLink(board.getMusic().getThumbNailLink())
                .build();
    }
}
