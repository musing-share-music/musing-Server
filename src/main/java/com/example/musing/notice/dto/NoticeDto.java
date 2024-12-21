package com.example.musing.notice.dto;

import lombok.Builder;
import lombok.Data;


import java.time.LocalDateTime;

@Data
@Builder
public class NoticeDto {
    private long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String username;
}
