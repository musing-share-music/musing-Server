package com.example.musing.report.service;

import com.example.musing.report.dto.ReportRequestDto;

public interface ReportService {
    void reportBoard(long boardId, ReportRequestDto.ReportBoardRequestDto reportBoardRequestDto);

    void reportReply(long replyId, ReportRequestDto.ReportReplyRequestDto reportReplyRequestDto);
}
