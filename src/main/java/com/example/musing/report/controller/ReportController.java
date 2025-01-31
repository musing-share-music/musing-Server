package com.example.musing.report.controller;

import com.example.musing.common.dto.ResponseDto;
import com.example.musing.notice.dto.NoticeDto;
import com.example.musing.report.dto.ReportRequestDto;
import com.example.musing.report.dto.ReportResponseDto;
import com.example.musing.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/musing")
@RequiredArgsConstructor
@RestController
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/report/reply")
    public ResponseDto<String> reportReply(@RequestParam long replyId, ReportRequestDto.ReportReplyRequestDto reportReplyRequestDto){
        reportService.reportReply(replyId, reportReplyRequestDto);
        return ResponseDto.of("","댓글을 신고했습니다.");
    }

    @PostMapping("/report/board")
    public ResponseDto<String> reportBoard(@RequestParam long boardId, ReportRequestDto.ReportBoardRequestDto reportBoardRequestDto){
        reportService.reportBoard(boardId, reportBoardRequestDto);
        return ResponseDto.of("","게시글을 신고했습니다.");
    }

    @GetMapping("/report/board/list")
    public ResponseDto<Page<ReportResponseDto.ReportBoardResponseDto>> reportBoardList(
            @RequestParam(name = "page", defaultValue = "1") int page){
        Page<ReportResponseDto.ReportBoardResponseDto> reportBoardList = reportService.getReportBoardList(page);
        return ResponseDto.of(reportBoardList);
    }

    @GetMapping("/report/reply/list")
    public ResponseDto<Page<ReportResponseDto.ReportReplyResponseDto>> reportReplyList(
            @RequestParam(name = "page", defaultValue = "1") int page){
        Page<ReportResponseDto.ReportReplyResponseDto> reportReplyList = reportService.getReportReplyList(page);
        return ResponseDto.of(reportReplyList);
    }
}
