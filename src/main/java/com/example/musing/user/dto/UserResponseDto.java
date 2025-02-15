package com.example.musing.user.dto;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.artist.entity.Artist;
import com.example.musing.board.dto.BoardListResponseDto;
import com.example.musing.board.entity.Board;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.genre.entity.Genre;
import com.example.musing.mood.dto.MoodDto;
import com.example.musing.mood.entity.Mood;
import com.example.musing.reply.dto.ReplyDto;
import com.example.musing.reply.entity.Reply;
import com.example.musing.user.entity.User;
import lombok.Builder;

import java.util.List;

public class UserResponseDto {
    @Builder
    public record UserInfoDto(
            String email,
            String name,
            String authority,
            int likeMusicCount,
            int myPlaylistCount
    ) {
        public static UserInfoDto of(User user, int likeMusicCount, int myPlaylistCount) {
            return UserInfoDto.builder()
                    .email(user.getEmail())
                    .name(user.getUsername())
                    .authority(user.getRole().name())
                    .likeMusicCount(likeMusicCount)
                    .myPlaylistCount(myPlaylistCount)
                    .build();
        }
    }

    @Builder
    public record UserInfoPageDto(
            String email,
            String name,
            List<GenreDto> likeGenre,
            List<MoodDto> likeMood,
            List<ArtistDto> likeArtist,
            List<BoardListResponseDto.BoardRecapDto> boardDtos,
            List<ReplyDto> replyDtos
    ) {
        public static UserInfoPageDto of(User user, List<Genre> genres, List<Mood> moods, List<Artist> artists,
                                         List<BoardListResponseDto.BoardRecapDto> boards, List<Reply> replies) {
            return UserInfoPageDto.builder()
                    .email(user.getEmail())
                    .name(user.getUsername())
                    .likeGenre(genres.stream().map(GenreDto::toDto).toList())
                    .likeMood(moods.stream().map(MoodDto::toDto).toList())
                    .likeArtist(artists.stream().map(ArtistDto::toDto).toList())
                    .boardDtos(boards)
                    .replyDtos(replies.stream().map(ReplyDto::from).toList())
                    .build();
        }
    }
}
