package com.example.musing.main.dto;

import com.example.musing.board.dto.BoardDto;
import com.example.musing.notice.dto.NoticeDto;
import lombok.Builder;

import java.util.List;

@Builder
public record NotLoginMainPageDto(
        NoticeDto noticeDto, //메인페이지 공지사항
        List<BoardDto.GenreBoardDto> recommendGenres, //랜덤 장르 하나중에 관련 게시글 최대 5개 가져오기
        BoardDto.HotMusicBoardDto hotMusicBoard, //음악 추천 게시글의 인기 곡 1개
        List<BoardDto.MainPageBoardDto> recentBoard //최신순 음악 추천 게시글 최대 5개 가져오기
) {
    public static NotLoginMainPageDto of(NoticeDto noticeDto,
                                         List<BoardDto.GenreBoardDto> recommendGenres,
                                         BoardDto.HotMusicBoardDto hotMusicBoard,
                                         List<BoardDto.MainPageBoardDto> recentBoard) {
        return NotLoginMainPageDto.builder()
                .noticeDto(noticeDto)
                .recommendGenres(recommendGenres)
                .hotMusicBoard(hotMusicBoard)
                .recentBoard(recentBoard)
                .build();
    }
}

