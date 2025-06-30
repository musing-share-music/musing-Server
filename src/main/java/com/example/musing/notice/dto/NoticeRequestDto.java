package com.example.musing.notice.dto;

import lombok.Builder;

@Builder
public record NoticeRequestDto(
        String title,
        String content) {
}
