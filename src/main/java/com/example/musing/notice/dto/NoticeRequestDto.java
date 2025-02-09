package com.example.musing.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record NoticeRequestDto(
        @Schema(description = "공지사항 제목", example = "안녕하세요. 음악 추천 사이트 Musing입니다.")
        String title,
        @Schema(description = "공지사항 내용", example = "여러분들의 다양한 음악 서비스를 제공하기 위해 현재 ....")
        String content) {
}
