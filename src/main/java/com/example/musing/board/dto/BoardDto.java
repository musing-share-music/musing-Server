package com.example.musing.board.dto;

import com.example.musing.board.entity.Board;
import lombok.Builder;

@Builder
public record BoardDto(
        long id, String title, String musicName, String artist,
        String thumbNailLink) { //메인 페이지 음악 추천 게시판 인기곡 부분과 좋아요한 음악
    public static BoardDto toDto(Board board) {
        return BoardDto.builder()
                .title(board.getTitle())
                .musicName(board.getMusicId().getName())
                .artist(board.getMusicId().getArtist())
                .thumbNailLink(board.getMusicId().getThumbNailLink())
                .build();
    }
}
