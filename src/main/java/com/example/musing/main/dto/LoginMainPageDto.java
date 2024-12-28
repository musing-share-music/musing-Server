package com.example.musing.main.dto;

import com.example.musing.board.dto.BoardDto;
import com.example.musing.board.dto.GenreBoardDto;
import com.example.musing.notice.dto.NoticeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;


@Schema(description = "로그인한 메인페이지")
@Builder
public record LoginMainPageDto(
        @Schema(description = "최신 공지사항 1개")
        NoticeDto noticeDto,
        @Schema(description = "내가 좋아하는 장르의 태그", example = "[\"블루스\", \"락\", \"발라드\"]")
        List<String> likeGenre,
        @Schema(description = "좋아요한 음악 10개, 아래 Dto에 한개 이상으로 있습니다.")
        List<BoardDto> likeMusicDtos,
        @Schema(description = "랜덤 장르 하나중에 관련 게시글 최대 5개, 아래 Dto에 한개 이상으로 있습니다.")
        List<GenreBoardDto> recommendGenres,
        @Schema(description = "음악 추천 게시글의 인기 곡 1개")
        BoardDto hotMusicBoard,
        @Schema(description = "최신순 음악 추천 게시글 최대 5개, 아래 Dto에 한개 이상으로 있습니다.")
        List<MainPageBoardDto> recentBoard
) {
    public static LoginMainPageDto of(NoticeDto noticeDto,
                                      List<String> likeGenre,
                                      List<BoardDto> likeMusicDtos,
                                      List<GenreBoardDto> recommendGenres,
                                      BoardDto hotMusicBoard,
                                      List<MainPageBoardDto> recentBoard) {
        return LoginMainPageDto.builder()
                .noticeDto(noticeDto)
                .likeGenre(likeGenre)
                .likeMusicDtos(likeMusicDtos)
                .recommendGenres(recommendGenres)
                .hotMusicBoard(hotMusicBoard)
                .recentBoard(recentBoard)
                .build();
    }
}
