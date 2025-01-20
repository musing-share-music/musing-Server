package com.example.musing.main.dto;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.board.entity.Board;
import com.example.musing.music.entity.Music;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record RecommendBoardRight(
        @Schema(description = "음악 추천 게시판 고유 ID", example = "7")
        long id,
        @Schema(description = "음악 추천 게시판 제목", example = "명곡은 28년이 지난 지금도 여전하다.")
        String title,
        @Schema(description = "작성자 이름", example = "닉네임할거없다")
        String username,
        @Schema(description = "작성일자", example = "2025-01-05 11:51:23.000000")
        LocalDateTime createdAt,
        @Schema(description = "벌점 및 댓글 수", example = "8")
        int replyCount,
        @Schema(description = "추천 수", example = "12")
        int recommendCount,
        @Schema(description = "조회 수", example = "231")
        int viewCount,
        @Schema(description = "노래 제목", example = "Smooth Criminal - Live in Munich 1997")
        String musicName,
        @Schema(description = "아티스트 Id 및 이름")
        List<ArtistDto> artists,
        @Schema(description = "유튜브 썸네일 사진 링크", example = "https://img.youtube.com/vi/4Aa9GwWaRv0/maxresdefault.jpg")
        String thumbNailLink) { //메인 페이지에 사용할 음악 추천 게시판 최신순5개
    public static RecommendBoardRight toDto(Board board) {
        return RecommendBoardRight.builder()
                .id(board.getId())
                .title(board.getTitle())
                .username(board.getUser().getUsername())
                .createdAt(board.getCreatedAt())
                .replyCount(board.getReplyCount())
                .recommendCount(board.getRecommendCount())
                .viewCount(board.getViewCount())
                .musicName(board.getMusic().getName())
                .artists(toDtoArtistList(board.getMusic()))
                .thumbNailLink(board.getMusic().getThumbNailLink())
                .build();
    }

    private static List<ArtistDto> toDtoArtistList(Music music){
        return music.getArtists().stream()
            .map(musicArtist -> ArtistDto.toDto(musicArtist.getArtist()))
            .toList();
    }
}
