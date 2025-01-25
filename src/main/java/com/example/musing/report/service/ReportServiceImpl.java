package com.example.musing.report.service;

import com.example.musing.board.entity.Board;
import com.example.musing.board.repository.BoardRepository;
import com.example.musing.exception.CustomException;
import com.example.musing.exception.ErrorCode;
import com.example.musing.reply.entity.Reply;
import com.example.musing.reply.repository.ReplyRepository;
import com.example.musing.report.dto.ReportRequestDto;
import com.example.musing.report.entity.ReportBoard;
import com.example.musing.report.entity.ReportReply;
import com.example.musing.report.repository.ReportBoardRepository;
import com.example.musing.report.repository.ReportReplyRepository;
import com.example.musing.user.entity.User;
import com.example.musing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.musing.exception.ErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReportServiceImpl implements ReportService{

    private final ReportBoardRepository reportBoardRepository;
    private final ReportReplyRepository reportReplyRepository;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    @Override
    @Transactional
    public void reportBoard(ReportRequestDto.ReportBoardRequestDto reportBoardRequestDto){
        ReportBoard reportBoard = ReportBoard.builder()
                .board(getBoard(reportBoardRequestDto.boardId()))
                .content(reportBoardRequestDto.content())
                .user(getUser())
                .build();

        reportBoardRepository.save(reportBoard);
    }
    @Override
    @Transactional
    public void reportReply(ReportRequestDto.ReportReplyRequestDto reportReplyRequestDto){
        ReportReply reportReply = ReportReply.builder()
                .reply(getReply(reportReplyRequestDto.replyId()))
                .content(reportReplyRequestDto.content())
                .user(getUser())
                .build();

        reportReplyRepository.save(reportReply);
    }

    private Board getBoard(long boardId){
        return boardRepository.findById(boardId).orElseThrow(() -> new CustomException(NOT_FOUND_BOARDID));
    }

    private Reply getReply(long replyId){
        return replyRepository.findById(replyId).orElseThrow(() -> new CustomException(NOT_FOUND_REPLY));
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }
}
