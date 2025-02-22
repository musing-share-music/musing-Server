package com.example.musing.reply.service;

import com.example.musing.reply.dto.ReplyDto;
import com.example.musing.reply.entity.Reply;
import org.springframework.data.domain.Page;

public interface ReplyService {

    void writeReply(long boardId, ReplyDto replyDto);

    ReplyDto findMyReplyByBoardId(long boardId);

    ReplyDto findMyReplyByReplyId(long replyId);

    void modifyReply(long replyId, ReplyDto replyDto);

    void deleteReply(long replyId);

    Page<ReplyDto> findReplies(long boardId, int page, String sortType, String sort);
}
