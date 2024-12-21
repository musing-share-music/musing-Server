package com.example.musing.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoardDto {
    @Builder
    public static class GenreBoardDto {//메인 페이지 장르 추천에 쓰임
        private long id; //board
        private String musicName; //music
        private String thumbNailLink; //music
    }

    @Builder
    public static class HotMusicBoardDto{ //메인 페이지 음악 추천 게시판 인기곡 하나
        private long id; //board
        private String title;
        private String musicName; //music
        private String artist;
        private String thumbNailLink;
    }

    @Builder
    public static class MainPageBoardDto{ //메인 페이지에 사용할 음악 추천 게시판 최신순5개
        private long id; //board
        private String title;
        private String content;
        private String username;
        private int replyCount;
        private int recommendCount;
        private int viewCount;
        private String musicName; //music
        private String artist;
        private String thumbNailLink;
    }
}
