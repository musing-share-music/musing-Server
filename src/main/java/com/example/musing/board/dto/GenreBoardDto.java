package com.example.musing.board.dto;

import com.example.musing.board.entity.Board;
import lombok.Builder;

@Builder
public record GenreBoardDto(long id, String musicName,
                            String thumbNailLink) { //메인 페이지 장르 추천에 쓰임
    public static GenreBoardDto toDto(Board board){
        return GenreBoardDto.builder()
                .id(board.getId())
                .musicName(board.getMusic().getName())
                .thumbNailLink(board.getMusic().getThumbNailLink())
                .build();
    }
}
