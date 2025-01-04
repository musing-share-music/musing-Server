package com.example.musing.reply.service;

import com.example.musing.reply.dto.ReplyDto;
import com.example.musing.reply.entity.Reply;
import org.springframework.data.domain.Page;

public interface ReplyService {

    void writeReply(ReplyDto replyDto, long boardId);

    ReplyDto findMyReply(long boardId);

    void modifyReply(long replyId, ReplyDto replyDto);

    void deleteReply(long replyId);

    Page<ReplyDto> findReplies(int page);
}
