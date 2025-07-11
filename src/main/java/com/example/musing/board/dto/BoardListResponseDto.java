package com.example.musing.board.dto;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.artist.entity.Artist_Music;
import com.example.musing.board.entity.Board;

import java.time.LocalDateTime;
import java.util.List;

import com.example.musing.genre.dto.GenreDto;
import com.example.musing.mood.dto.MoodDto;
import lombok.Builder;
import org.springframework.data.domain.Page;

//nested record class 사용

public class BoardListResponseDto {

    @Builder
    public record RecommendBoardFirstDto(
        String title,
        String content,
        String musicName,
        List<ArtistDto> artists,
        float rating,
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
        String title,
        String musicName,
        List<ArtistDto> artists,
        String thumbNailLink,
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
        long id,
        String title,
        String musicName,
        List<ArtistDto> artists,
        float rating,
        int replyCount,
        String thumbNailLink,
        List<GenreDto> genreList,
        List<MoodDto> moodList
    ) {
        public static BoardDto toDto(Board board , List<GenreDto> genreList, List<MoodDto> moodList, List<ArtistDto> artists) {
            return BoardDto.builder()
                .id(board.getId())
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
