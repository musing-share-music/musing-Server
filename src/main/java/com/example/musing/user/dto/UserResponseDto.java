package com.example.musing.user.dto;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.board.dto.BoardListResponseDto;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.mood.dto.MoodDto;
import com.example.musing.reply.dto.ReplyResponseDto;
import com.example.musing.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

public class UserResponseDto {
    @Builder
    public record UserProfileDto(
            String name,
            String email,
            String profileUrl
    ) {
        public static UserProfileDto of(User user) {
            return UserProfileDto.builder()
                    .name(user.getUsername())
                    .email(user.getEmail())
                    .profileUrl(user.getProfile())
                    .build();
        }
    }

    @Builder
    public record UserInfoDto(
            String userId,
            String email,
            String name,
            String authority,
            int likeMusicCount,
            int myPlaylistCount
    ) {
        public static UserInfoDto of(User user, int likeMusicCount, int myPlaylistCount) {
            return UserInfoDto.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .name(user.getUsername())
                    .authority(user.getRole().name())
                    .likeMusicCount(likeMusicCount)
                    .myPlaylistCount(myPlaylistCount)
                    .build();
        }
    }

    @Schema(description = "유저 회원 정보")
    @Builder
    public record UserInfoPageDto(

            @Schema(description = "유저 이메일")
            String email,
            @Schema(description = "유저 닉네임")
            String name,
            @Schema(description = "프로필 Url")
            String profile,
            @Schema(description = "유저가 좋아하는 장르")
            List<GenreDto> likeGenre,
            @Schema(description = "유저가 좋아하는 분위기")
            List<MoodDto> likeMood,
            @Schema(description = "유저가 좋아하는 가수")
            List<ArtistDto> likeArtist,
            @Schema(description = "자신이 작성한 음악 추천 게시글")
            List<BoardListResponseDto.BoardRecapDto> boardDtos,
            @Schema(description = "자신이 작성한 별점 및 리뷰")
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
