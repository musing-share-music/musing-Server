package com.example.musing.admin.board.dto;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.board.entity.Board;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.mood.dto.MoodDto;
import lombok.Builder;

import java.util.List;

public class AdminBoardResponseDto {
    @Builder
    public record BoardDto(
            String title,
            String musicName,
            List<ArtistDto> artists,
            float rating,
            int replyCount,
            String thumbNailLink,
            List<GenreDto> genreList,
            List<MoodDto> moodList
    ) {
        public static AdminBoardResponseDto.BoardDto toDto(Board board , List<GenreDto> genreList, List<MoodDto> moodList, List<ArtistDto> artists) {
            return AdminBoardResponseDto.BoardDto.builder()
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
}
