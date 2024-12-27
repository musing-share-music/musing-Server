package com.example.musing.main.dto;

import com.example.musing.board.entity.Board;
import lombok.Builder;

@Builder
public record MainPageBoardDto(
        long id, String title, String content, String username,
        int replyCount, int recommendCount, int viewCount,
        String musicName, String artist,
        String thumbNailLink) { //메인 페이지에 사용할 음악 추천 게시판 최신순5개
    public static MainPageBoardDto toDto(Board board) {
        return MainPageBoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .username(board.getUser_id().getUsername())
                .replyCount(board.getReplies().size()) //해당부분은 조회 최적화를 위해 Count만 가져올 예정
                .recommendCount(board.getRecommendCount())
                .viewCount(board.getViewCount())
                .musicName(board.getMusic_id().getName())
                .artist(board.getMusic_id().getArtist())
                .thumbNailLink(board.getMusic_id().getThumbNailLink())
                .build();
    }
}
