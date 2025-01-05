package com.example.musing.board.dto;

import lombok.Data;

@Data
public class MusicBoardDto {
    private Long id;
    private Long userId;
    private String musicName;
    private String youtubeLink;
    private String genre;
    private String description;
    private int likes;
    private String createdAt;
}
