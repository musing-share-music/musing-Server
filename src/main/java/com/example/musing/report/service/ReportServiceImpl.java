package com.example.musing.report.service;

import com.example.musing.board.entity.Board;
import com.example.musing.board.repository.BoardRepository;
import com.example.musing.exception.CustomException;
import com.example.musing.reply.entity.Reply;
import com.example.musing.reply.repository.ReplyRepository;
import com.example.musing.report.dto.ReportRequestDto;
import com.example.musing.report.dto.ReportResponseDto;
import com.example.musing.report.entity.ReportBoard;
import com.example.musing.report.entity.ReportReply;
import com.example.musing.report.repository.ReportBoardRepository;
import com.example.musing.report.repository.ReportReplyRepository;
import com.example.musing.user.entity.User;
import com.example.musing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.musing.exception.ErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReportServiceImpl implements ReportService {

    private static int PAGESIZE = 10;
    private final ReportBoardRepository reportBoardRepository;
    private final ReportReplyRepository reportReplyRepository;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void reportBoard(long boardId, ReportRequestDto.ReportBoardRequestDto reportBoardRequestDto) {
        ReportBoard reportBoard = ReportBoard.builder()
                .board(getBoard(boardId))
                .content(reportBoardRequestDto.content())
                .user(getUser())
                .build();

        reportBoardRepository.save(reportBoard);
    }

    @Override
    @Transactional
    public void reportReply(long replyId, ReportRequestDto.ReportReplyRequestDto reportReplyRequestDto) {
        ReportReply reportReply = ReportReply.builder()
                .reply(getReply(replyId))
                .content(reportReplyRequestDto.content())
                .user(getUser())
                .build();

        reportReplyRepository.save(reportReply);
    }

    @Override
    public Page<ReportResponseDto.ReportBoardResponseDto> getReportBoardList(int page) {
        if (page < 1) { // 잘못된 접근으로 throw할때 쿼리문 실행을 안하기 위해 나눠서 체크
            throw new CustomException(BAD_REQUEST_REPORT_PAGE);
        }

        Pageable pageable = PageRequest.of(page - 1, PAGESIZE);
        Page<ReportBoard> boards = reportBoardRepository.findAll(pageable);

        int totalPages = boards.getTotalPages();

        if (page - 1 > totalPages) {
            throw new CustomException(BAD_REQUEST_REPORT_PAGE);
        }

        return boards.map(this::entityToDto);
    }

    @Override
    public Page<ReportResponseDto.ReportReplyResponseDto> getReportReplyList(int page) {
        if (page < 1) { // 잘못된 접근으로 throw할때 쿼리문 실행을 안하기 위해 나눠서 체크
            throw new CustomException(BAD_REQUEST_REPORT_PAGE);
        }

        Pageable pageable = PageRequest.of(page - 1, PAGESIZE);
        Page<ReportReply> replies = reportReplyRepository.findAll(pageable);

        int totalPages = replies.getTotalPages();

        if (page - 1 > totalPages) {
            throw new CustomException(BAD_REQUEST_REPORT_PAGE);
        }

        return replies.map(this::entityToDto);
    }

    @Override
    @Transactional
    public void deleteBoard(long boardId) {
        deleteBoardReports(boardId);

        Board board = boardRepository.findById(boardId).orElseThrow(() -> new CustomException(NOT_FOUND_BOARD));
        board.delete();
    }

    @Override
    @Transactional
    public void deleteReply(long replyId) {
        deleteReplyReports(replyId);

        Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new CustomException(NOT_FOUND_REPLY));
        replyRepository.delete(reply);
    }

    private void deleteBoardReports(long boardId) {
        List<ReportBoard> reports = reportBoardRepository.findByBoard_Id(boardId);
        reports.forEach(ReportBoard::delete);

        reportBoardRepository.saveAll(reports);
    }

    private void deleteReplyReports(long replyId) {
        List<ReportReply> reports = reportReplyRepository.findByReply_Id(replyId);
        reports.forEach(ReportReply::delete);

        reportReplyRepository.saveAll(reports);
    }

    private Board getBoard(long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new CustomException(NOT_FOUND_BOARD));
    }

    private Reply getReply(long replyId) {
        return replyRepository.findById(replyId).orElseThrow(() -> new CustomException(NOT_FOUND_REPLY));
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("email: " + authentication.getName());
        return userRepository.findById(authentication.getName()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }

    private ReportResponseDto.ReportBoardResponseDto entityToDto(ReportBoard reportBoard) {
        return ReportResponseDto.ReportBoardResponseDto.toDto(reportBoard);
    }

    private ReportResponseDto.ReportReplyResponseDto entityToDto(ReportReply reportReply) {
        return ReportResponseDto.ReportReplyResponseDto.toDto(reportReply);
    }
}
