package com.example.musing.reply.dto;

import com.example.musing.reply.entity.Reply;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;

@Builder
public record ReplyDto(
    String starScore,
    String content
) {
    public static ReplyDto from(Reply reply){
        return ReplyDto.builder()
            .starScore(reply.getStarScore())
            .content(reply.getContent())
            .build();
    }
}
