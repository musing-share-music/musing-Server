package com.example.musing.reply.service;

import com.example.musing.board.dto.BoardReplyDto;
import com.example.musing.board.entity.Board;
import com.example.musing.reply.dto.ReplyRequestDto;
import com.example.musing.reply.dto.ReplyResponseDto;
import com.example.musing.reply.entity.Reply;
import org.springframework.data.domain.Page;

public interface ReplyService {
    Reply findByReplyId(long replyId);

    ReplyResponseDto.ReplyAndUpdatedBoardDto writeReply(long boardId, ReplyRequestDto replyDto);

    ReplyResponseDto.ReplyDto findMyReplyByBoardId(long boardId);

    ReplyResponseDto.ReplyDto findMyReplyByReplyId(long replyId);

    BoardReplyDto modifyReply(long replyId, ReplyRequestDto replyDto);

    BoardReplyDto deleteReply(long replyId);

    Page<ReplyResponseDto.ReplyDto> findReplies(long boardId, int page, String sortType, String sort);
}
