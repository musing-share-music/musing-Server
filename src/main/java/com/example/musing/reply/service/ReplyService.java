package com.example.musing.reply.service;

import com.example.musing.reply.dto.ReplyRequestDto;
import com.example.musing.reply.dto.ReplyResponseDto;
import org.springframework.data.domain.Page;

public interface ReplyService {

    void writeReply(long boardId, ReplyRequestDto replyDto);

    ReplyResponseDto.ReplyDto findMyReplyByBoardId(long boardId);

    ReplyResponseDto.ReplyDto findMyReplyByReplyId(long replyId);

    void modifyReply(long replyId, ReplyRequestDto replyDto);

    void deleteReply(long replyId);

    Page<ReplyResponseDto.ReplyDto> findReplies(long boardId, int page, String sortType, String sort);
}
