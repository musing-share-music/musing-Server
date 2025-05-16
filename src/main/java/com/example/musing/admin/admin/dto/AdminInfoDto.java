package com.example.musing.admin.admin.dto;

import com.example.musing.user.entity.User;
import lombok.Builder;

@Builder
public record AdminInfoDto (
        String userId,
        String email,
        String name,
        String authority
) {
    public static AdminInfoDto of(User user) {
        return AdminInfoDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getUsername())
                .authority(user.getRole().name())
                .build();
    }
}
