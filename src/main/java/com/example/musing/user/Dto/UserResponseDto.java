package com.example.musing.user.Dto;

import com.example.musing.user.entity.User;
import lombok.Builder;

public class UserResponseDto {
    @Builder
    public record UserInfoDto(
        String email,
        String name,
        String authority,
        int likeMusicCount,
        int myPlaylistCount
    ){
        public static UserInfoDto of(User user,int likeMusicCount, int myPlaylistCount){
            return UserInfoDto.builder()
                    .email(user.getEmail())
                    .name(user.getUsername())
                    .authority(user.getRole().name())
                    .likeMusicCount(likeMusicCount)
                    .myPlaylistCount(myPlaylistCount)
                    .build();
        }
    }
}
