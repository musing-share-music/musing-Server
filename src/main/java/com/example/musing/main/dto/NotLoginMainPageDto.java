package com.example.musing.main.dto;

import com.example.musing.board.dto.RecommendBoardLeft;
import com.example.musing.board.dto.GenreBoardDto;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.notice.dto.NoticeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
@Schema(description = "로그인하지 않은 메인페이지")
@Builder
public record NotLoginMainPageDto(
        NoticeDto noticeDto, //메인페이지 공지사항
        GenreDto recommendGenre,//랜덤 장르 이름
        List<GenreBoardDto> recommendGenres, //랜덤 장르 하나중에 관련 게시글 최대 5개 가져오기
        RecommendBoardLeft hotMusicBoard, //음악 추천 게시글의 인기 곡 1개
        List<RecommendBoardRight> recentBoard, //최신순 음악 추천 게시글 최대 5개 가져오기
        String passModal //모달창 입력을 다 하였나 여부
) {
    public static NotLoginMainPageDto of(NoticeDto noticeDto,
                                         GenreDto recommendGenre,
                                         List<GenreBoardDto> recommendGenres,
                                         RecommendBoardLeft hotMusicBoard,
                                         List<RecommendBoardRight> recentBoard,
                                         String passModal) {
        return NotLoginMainPageDto.builder()
                .noticeDto(noticeDto)
                .recommendGenre(recommendGenre)
                .recommendGenres(recommendGenres)
                .hotMusicBoard(hotMusicBoard)
                .recentBoard(recentBoard)
                .passModal(passModal)
                .build();
    }
}

