package com.example.musing.notice.dto;

import com.example.musing.notice.entity.Notice;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NoticeDto(long id, String title, String content,
                        LocalDateTime createdAt, String username) {
    public static NoticeDto toDto(Notice notice) {
        return NoticeDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(notice.getCreatedAt())
                .username(notice.getUser().getUsername())
                .build();
    }
}
