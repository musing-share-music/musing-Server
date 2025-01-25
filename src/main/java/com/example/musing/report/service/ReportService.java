package com.example.musing.report.service;

import com.example.musing.report.dto.ReportRequestDto;
import com.example.musing.report.entity.ReportBoard;
import com.example.musing.report.entity.ReportReply;
import org.springframework.transaction.annotation.Transactional;

public interface ReportService {
    void reportBoard(ReportRequestDto.ReportBoardRequestDto reportBoardRequestDto);
    void reportReply(ReportRequestDto.ReportReplyRequestDto reportReplyRequestDto);
}
