package com.example.musing.report.dto;

import lombok.Builder;

public class ReportRequestDto{
    @Builder
    public record ReportBoardRequestDto(
            String content,
            Long boardId
    ) {}
    @Builder
    public record ReportReplyRequestDto(
            String content,
            Long replyId
    ) {}
}
