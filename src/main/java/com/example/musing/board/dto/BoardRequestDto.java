package com.example.musing.board.dto;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.artist.entity.Artist_Music;
import com.example.musing.board.entity.Board;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.mood.dto.MoodDto;
import com.example.musing.music.entity.Music;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class BoardRequestDto {
    @Builder // 관리자 확인,
    public record BoardDto(
            String title,
            String musicName,
            float rating,
            String username,
            LocalDateTime createAt,
            LocalDateTime updateAt,
            String youtubeLink,
            int replyCount,
            String thumbNailLink,
            String content,
            String imageUrl,
            List<ArtistDto> artists,
            List<GenreDto> genreList,
            List<MoodDto> moodList
    ) {
        public static BoardRequestDto.BoardDto toDto(Board board, List<GenreDto> genreList, List<MoodDto> moodList) {
            return BoardDto.builder()
                    .title(board.getTitle())
                    .musicName(board.getMusic().getName())
                    .artists(board.getMusic().getArtists().stream()
                            .map(Artist_Music::getArtist)
                            .map(ArtistDto::toDto).toList())
                    .rating(board.getRating())
                    .username(board.getUser().getUsername())
                    .createAt(board.getCreatedAt())
                    .updateAt(board.getUpdatedAt())
                    .youtubeLink(board.getMusic().getSongLink())
                    .replyCount(board.getReplyCount())
                    .thumbNailLink(board.getMusic().getThumbNailLink())
                    .content(board.getContent())
                    .imageUrl(board.getImage())
                    .genreList(genreList)
                    .moodList(moodList)
                    .build();
        }
    }
}
