package com.example.musing.board.dto;

import com.example.musing.reply.dto.ReplyRequestDto;
import com.example.musing.reply.dto.ReplyResponseDto;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record BoardAndReplyPageDto(
        BoardRequestDto.BoardDto boardDto,
        Page<ReplyResponseDto.ReplyDto> replyDtos
) {
    public static BoardAndReplyPageDto of(BoardRequestDto.BoardDto boardDto, Page<ReplyResponseDto.ReplyDto> replyDtos){
        return BoardAndReplyPageDto.builder()
                .boardDto(boardDto)
                .replyDtos(replyDtos)
                .build();
    }
}
