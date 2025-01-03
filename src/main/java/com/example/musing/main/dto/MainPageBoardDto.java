package com.example.musing.main.dto;

import com.example.musing.board.entity.Board;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record MainPageBoardDto(
        @Schema(description = "음악 추천 게시판 고유 ID", example = "7")
        long id,

        @Schema(description = "음악 추천 게시판 제목", example = "명곡은 28년이 지난 지금도 여전하다.")
        String title,
        @Schema(description = "음악 추천 게시판 내용",
                example = "Michael Jackson의 \"Smooth Criminal\"은 그의 독보적인 무대 퍼포먼스와 함께 전설적인 곡으로," +
                        " 1997년 뮌헨 공연에서는 완벽한 춤과 카리스마 넘치는 라이브를 선보였습니다. ")
        String content,
        @Schema(description = "작성자 이름", example = "닉네임할거없다")
        String username,
        @Schema(description = "벌점 및 댓글 수", example = "8")
        int replyCount,
        @Schema(description = "추천 수", example = "12")
        int recommendCount,
        @Schema(description = "조회 수", example = "231")
        int viewCount,
        @Schema(description = "노래 제목", example = "Smooth Criminal - Live in Munich 1997")
        String musicName,
        @Schema(description = "아티스트 명", example = "Michael Jackson")
        String artist,
        @Schema(description = "유튜브 썸네일 사진 링크", example = "https://img.youtube.com/vi/4Aa9GwWaRv0/maxresdefault.jpg")
        String thumbNailLink) { //메인 페이지에 사용할 음악 추천 게시판 최신순5개
    public static MainPageBoardDto toDto(Board board) {
        return MainPageBoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .username(board.getUser().getUsername())
                .replyCount(board.getReplies().size()) //해당부분은 조회 최적화를 위해 Count만 가져올 예정
                .recommendCount(board.getRecommendCount())
                .viewCount(board.getViewCount())
                .musicName(board.getMusic().getName())
                .artist(board.getMusic().getArtist())
                .thumbNailLink(board.getMusic().getThumbNailLink())
                .build();
    }
}
