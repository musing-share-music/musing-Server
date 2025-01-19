package com.example.musing.main.dto;

import com.example.musing.board.dto.GenreBoardDto;
import com.example.musing.board.dto.HotBoardDto;
import com.example.musing.genre.dto.GenreDto;
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
        List<GenreDto> likeGenre,
        @Schema(description = "좋아요한 음악 10개, 아래 Dto에 한개 이상으로 있습니다.")
        List<GenreBoardDto> likeMusicDtos,
        @Schema(description = "랜덤 장르 이름.")
        GenreDto recommendGenre,//랜덤 장르 이름
        @Schema(description = "랜덤 장르 하나중에 관련 게시글 최대 5개.")
        List<GenreBoardDto> recommendGenres,
        @Schema(description = "음악 추천 게시글의 인기 곡 1개")
        HotBoardDto hotMusicBoard,
        @Schema(description = "최신순 음악 추천 게시글 최대 5개, 아래 Dto에 한개 이상으로 있습니다.")
        List<MainPageBoardDto> recentBoard,
        @Schema(description = "최초 로그인 이후 모달창 입력을 다 맞췄는지 확인합니다.")
        String modalCheck
) {
    public static LoginMainPageDto of(NoticeDto noticeDto,
                                      List<GenreDto> likeGenre,
                                      List<GenreBoardDto> likeMusicDtos,
                                      GenreDto recommendGenre, List<GenreBoardDto> recommendGenres,
                                      HotBoardDto hotMusicBoard,
                                      List<MainPageBoardDto> recentBoard,
                                      String modalCheck) {
        return LoginMainPageDto.builder()
                .noticeDto(noticeDto)
                .likeGenre(likeGenre)
                .likeMusicDtos(likeMusicDtos)
                .recommendGenre(recommendGenre)
                .recommendGenres(recommendGenres)
                .hotMusicBoard(hotMusicBoard)
                .recentBoard(recentBoard)
                .modalCheck(modalCheck)
                .build();
    }
}
