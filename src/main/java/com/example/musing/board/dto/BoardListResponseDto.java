package com.example.musing.board.dto;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.artist.entity.Artist_Music;
import com.example.musing.board.entity.Board;

import java.time.LocalDateTime;
import java.util.List;

import com.example.musing.genre.dto.GenreDto;
import com.example.musing.mood.dto.MoodDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.data.domain.Page;

//nested record class 사용

public class BoardListResponseDto {

    @Builder
    public record RecommendBoardFirstDto(
        @Schema(description = "게시글의 제목")
        String title,
        @Schema(description = "게시글의 내용")
        String content,
        @Schema(description = "게시글의 음악")
        String musicName,
        @Schema(description = "게시글의 아티스트 리스트")
        List<ArtistDto> artists,
        @Schema(description = "평균 별점")
        float rating,
        @Schema(description = "게시글의 분위기")
        String thumbNailLink
    ) {
        public static RecommendBoardFirstDto toDto(Board board, List<ArtistDto> artists) {
            return RecommendBoardFirstDto.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .musicName(board.getMusic().getName())
                .artists(artists)
                .rating(board.getRating())
                .thumbNailLink(board.getMusic().getThumbNailLink())
                .build();
        }
    }

    @Builder
    public record BoardRecapDto(
        @Schema(description = "게시글의 제목")
        String title,
        @Schema(description = "게시글의 음악")
        String musicName,
        @Schema(description = "게시글의 아티스트 리스트")
        List<ArtistDto> artists,
        @Schema(description = "게시글 썸네일 링크")
        String thumbNailLink,
        @Schema(description = "게시글 작성일자")
        LocalDateTime createAt
    ) {
        public static BoardRecapDto toDto(Board board) {
            return BoardRecapDto.builder()
                .title(board.getTitle())
                .musicName(board.getMusic().getName())
                .artists(board.getMusic().getArtists().stream()
                        .map(Artist_Music::getArtist)
                        .map(ArtistDto::toDto).toList())
                .thumbNailLink(board.getMusic().getThumbNailLink())
                .createAt(board.getCreatedAt())
                .build();
        }
    }

    @Builder
    public record BoardDto(
        @Schema(description = "게시글 제목", example = "추천하는 오늘의 노래")
        String title,
        @Schema(description = "노래 제목", example = "For the Love of God")
        String musicName,
        List<ArtistDto> artists,
        @Schema(description = "별점", example = "4.5")
        float rating,
        @Schema(description = "댓글 수", example = "11")
        int replyCount,
        @Schema(description = "유튜브 영상 썸네일 링크")
        String thumbNailLink,
        @Schema(description = "게시글의 장르")
        List<GenreDto> genreList,
        @Schema(description = "게시글의 분위기")
        List<MoodDto> moodList
    ) {
        public static BoardDto toDto(Board board , List<GenreDto> genreList, List<MoodDto> moodList, List<ArtistDto> artists) {
            return BoardDto.builder()
                .title(board.getTitle())
                .musicName(board.getMusic().getName())
                .artists(artists)
                .rating(board.getRating())
                .replyCount(board.getReplyCount())
                .thumbNailLink(board.getMusic().getThumbNailLink())
                .genreList(genreList)
                .moodList(moodList)
                .build();
        }
    }

    @Builder
    public record BoardPopUpDto(
        RecommendBoardFirstDto recommendBoardFirstDto,
        List<BoardRecapDto> recommendBoardListDto
    ) {
        public static BoardPopUpDto of(RecommendBoardFirstDto boardFirstDto, List<BoardRecapDto> recommendBoardListDto) {
            return BoardPopUpDto.builder()
                    .recommendBoardFirstDto(boardFirstDto)
                    .recommendBoardListDto(recommendBoardListDto)
                    .build();
        }
    }
    @Builder
    public record BoardListDto(
        BoardPopUpDto boardPopUpDto,
        Page<BoardDto> boardDtos
    ){
        public static BoardListDto of(BoardPopUpDto boardPopUpDto, Page<BoardDto> boardDtos) {
            return BoardListDto.builder()
                    .boardPopUpDto(boardPopUpDto)
                    .boardDtos(boardDtos)
                    .build();
        }
    }
}
