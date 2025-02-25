package com.example.musing.user.dto;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.board.dto.BoardListResponseDto;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.mood.dto.MoodDto;
import com.example.musing.reply.dto.ReplyResponseDto;
import com.example.musing.user.entity.User;
import lombok.Builder;

import java.util.List;

public class UserResponseDto {
    @Builder
    public record UserProfileDto(
            String name,
            String profileUrl
    ) {
        public static UserProfileDto of(User user) {
            return UserProfileDto.builder()
                    .name(user.getUsername())
                    .profileUrl(user.getProfile())
                    .build();
        }
    }

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
            String profile,
            List<GenreDto> likeGenre,
            List<MoodDto> likeMood,
            List<ArtistDto> likeArtist,
            List<BoardListResponseDto.BoardRecapDto> boardDtos,
            List<ReplyResponseDto.MyReplyDto> replyDtos
    ) {
        public static UserInfoPageDto of(User user, List<GenreDto> genres, List<MoodDto> moods, List<ArtistDto> artists,
                                         List<BoardListResponseDto.BoardRecapDto> boards, List<ReplyResponseDto.MyReplyDto> replies) {
            return UserInfoPageDto.builder()
                    .email(user.getEmail())
                    .name(user.getUsername())
                    .profile(user.getProfile())
                    .likeGenre(genres)
                    .likeMood(moods)
                    .likeArtist(artists)
                    .boardDtos(boards)
                    .replyDtos(replies)
                    .build();
        }
    }
}
