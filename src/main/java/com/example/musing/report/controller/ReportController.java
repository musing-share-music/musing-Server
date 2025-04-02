package com.example.musing.report.controller;

import com.example.musing.common.dto.ResponseDto;
import com.example.musing.report.dto.ReportRequestDto;
import com.example.musing.report.dto.ReportResponseDto;
import com.example.musing.report.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Tag(name = "신고 관련 API (관리자 기능 포함)")
@RequestMapping("/musing")
@RequiredArgsConstructor
@RestController
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/report/reply")
    public ResponseDto<String> reportReply(@RequestParam long replyId, ReportRequestDto.ReportReplyRequestDto reportReplyRequestDto) {
        reportService.reportReply(replyId, reportReplyRequestDto);
        return ResponseDto.of("", "댓글을 신고했습니다.");
    }

    @PostMapping("/report/board")
    public ResponseDto<String> reportBoard(@RequestParam long boardId, ReportRequestDto.ReportBoardRequestDto reportBoardRequestDto) {
        reportService.reportBoard(boardId, reportBoardRequestDto);
        return ResponseDto.of("", "게시글을 신고했습니다.");
    }

    @GetMapping("/admin/report/board/list")
    public ResponseDto<Page<ReportResponseDto.ReportBoardResponseDto>> reportBoardList(
            @RequestParam(name = "page", defaultValue = "1") int page) {
        Page<ReportResponseDto.ReportBoardResponseDto> reportBoardList = reportService.getReportBoardList(page);
        return ResponseDto.of(reportBoardList);
    }

    @GetMapping("/admin/report/board/list/search")
    public ResponseDto<Page<ReportResponseDto.ReportBoardResponseDto>> reportBoardListByKeyword(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "searchType") String searchType,
            @RequestParam(name = "keyword") String keyword) {

        Page<ReportResponseDto.ReportBoardResponseDto> reportBoardList =
                reportService.getSearchReportBoardList(page, searchType, keyword);
        return ResponseDto.of(reportBoardList);
    }

    @GetMapping("/admin/report/reply/list")
    public ResponseDto<Page<ReportResponseDto.ReportReplyResponseDto>> reportReplyList(
            @RequestParam(name = "page", defaultValue = "1") int page) {
        Page<ReportResponseDto.ReportReplyResponseDto> reportReplyList = reportService.getReportReplyList(page);
        return ResponseDto.of(reportReplyList);
    }

    @GetMapping("/admin/report/reply/list/search")
    public ResponseDto<Page<ReportResponseDto.ReportReplyResponseDto>> reportReplyListByKeyword(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "searchType") String searchType,
            @RequestParam(name = "keyword") String keyword) {

        Page<ReportResponseDto.ReportReplyResponseDto> reportReplyList =
                reportService.getSearchReportReplyList(page, searchType, keyword);
        return ResponseDto.of(reportReplyList);
    }

    @PutMapping("/admin/report/board")
    public ResponseDto<String> deleteBoard(@RequestParam long boardId) {
        reportService.deleteBoard(boardId);
        return ResponseDto.of("", "성공적으로 게시글을 삭제했습니다.");
    }

    //삭제와 수정이 섞여있어 Put으로 사용
    @PutMapping("/admin/report/reply")
    public ResponseDto<String> deleteReply(@RequestParam long replyId) {
        reportService.deleteReply(replyId);
        return ResponseDto.of("", "성공적으로 댓글을 삭제했습니다.");
    }
}
