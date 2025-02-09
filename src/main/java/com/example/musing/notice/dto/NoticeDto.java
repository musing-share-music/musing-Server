package com.example.musing.notice.dto;

import com.example.musing.notice.entity.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record NoticeDto(
        @Schema(description = "공지사항 고유 ID", example = "1")
        long id,
        @Schema(description = "공지사항 제목", example = "안녕하세요. 음악 추천 사이트 Musing입니다.")
        String title,
        @Schema(description = "공지사항 내용", example = "여러분들의 다양한 음악 서비스를 제공하기 위해 현재 ....")
        String content,
        @Schema(description = "공지사항 생성일자", example = "2024-12-28")
        LocalDateTime createdAt,
        @Schema(description = "공지사항 작성자", example = "관리자")
        String username,
        @Schema(description = "이미지 경로")
        List<String> imageUrl


) {
    public static NoticeDto from(Notice notice) {
        return NoticeDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(notice.getCreatedAt())
                .username(notice.getUser().getUsername())
                .imageUrl(notice.getImages())
                .build();
    }
}
