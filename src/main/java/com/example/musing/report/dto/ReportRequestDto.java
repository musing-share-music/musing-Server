package com.example.musing.report.dto;

import lombok.Builder;

public class ReportRequestDto{
    @Builder
    public record ReportBoardRequestDto(
            String content
    ) {}
    @Builder
    public record ReportReplyRequestDto(
            String content
    ) {}
}
