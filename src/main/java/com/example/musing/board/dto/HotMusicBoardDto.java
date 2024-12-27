package com.example.musing.board.dto;

import com.example.musing.board.entity.Board;
import lombok.Builder;

@Builder
public record HotMusicBoardDto(
        long id, String title, String musicName, String artist,
        String thumbNailLink) { //메인 페이지 음악 추천 게시판 인기곡 하나
    public static HotMusicBoardDto toDto(Board board){
        return HotMusicBoardDto.builder()
                .title(board.getTitle())
                .musicName(board.getMusic_id().getName())
                .artist(board.getMusic_id().getArtist())
                .thumbNailLink(board.getMusic_id().getThumbNailLink())
                .build();
    }
}
