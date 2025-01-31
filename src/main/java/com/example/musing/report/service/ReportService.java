package com.example.musing.report.service;

import com.example.musing.report.dto.ReportRequestDto;
import com.example.musing.report.dto.ReportResponseDto;
import org.springframework.data.domain.Page;

public interface ReportService {
    void reportBoard(long boardId, ReportRequestDto.ReportBoardRequestDto reportBoardRequestDto);

    void reportReply(long replyId, ReportRequestDto.ReportReplyRequestDto reportReplyRequestDto);

    Page<ReportResponseDto.ReportBoardResponseDto> getReportBoardList(int page);

    Page<ReportResponseDto.ReportReplyResponseDto> getReportReplyList(int page);

    void deleteBoard(long boardId);

    void deleteReply(long replyId);
}
