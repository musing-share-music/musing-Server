package com.example.musing.report.dto;

import com.example.musing.board.entity.Board;
import com.example.musing.notice.dto.NoticeDto;
import com.example.musing.notice.entity.Notice;
import com.example.musing.report.entity.ReportBoard;
import com.example.musing.report.entity.ReportReply;
import com.example.musing.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

public class ReportResponseDto {
    @Builder
    public record ReportBoardResponseDto(
        long reportId,
        String reportDate,
        String content,
        String username,
        Long boardId,
        String boardTitle
    ) {
        public static ReportBoardResponseDto toDto(ReportBoard reportBoard) {
            return ReportBoardResponseDto.builder()
                    .reportId(reportBoard.getId())
                    .reportDate(reportBoard.getReportDate().toString())
                    .content(reportBoard.getContent())
                    .username(reportBoard.getUser().getUsername())
                    .boardId(reportBoard.getBoard().getId())
                    .boardTitle(reportBoard.getBoard().getTitle())
                    .build();
        }
    }
    @Builder
    public record ReportReplyResponseDto(
            long reportId,
            String reportDate,
            String content,
            String username,
            Long replyId,
            String replyContent
    ) {
        public static ReportReplyResponseDto toDto(ReportReply reportReply) {
            return ReportReplyResponseDto.builder()
                    .reportId(reportReply.getId())
                    .reportDate(reportReply.getReportDate().toString())
                    .content(reportReply.getContent())
                    .username(reportReply.getUser().getUsername())
                    .replyId(reportReply.getReply().getId())
                    .replyContent(reportReply.getReply().getContent())
                    .build();
        }
    }
}
