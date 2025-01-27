package com.example.musing.report.service;

import com.example.musing.report.dto.ReportRequestDto;

public interface ReportService {
    void reportBoard(ReportRequestDto.ReportBoardRequestDto reportBoardRequestDto);

    void reportReply(ReportRequestDto.ReportReplyRequestDto reportReplyRequestDto);
}
