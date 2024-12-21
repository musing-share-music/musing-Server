package com.example.musing.main.dto;

import com.example.musing.board.dto.BoardDto;
import com.example.musing.notice.dto.NoticeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class MainPageDto {
    @Builder
    @Getter
    public static class NotLoginMainPageDto {
        private NoticeDto noticeDto; //메인페이지 공지사항
        private List<BoardDto.GenreBoardDto> recommendGenres; //랜덤 장르 하나중에 관련 게시글 최대 5개 가져오기
        private BoardDto.HotMusicBoardDto hotMusicBoard; //음악 추천 게시글의 인기 곡 1개
        private List<BoardDto.MainPageBoardDto> recentBoard; //최신순 음악 추천 게시글 최대 5개 가져오기

    }

    @Builder
    @Getter
    public static class LoginMainPageDto {}
}
