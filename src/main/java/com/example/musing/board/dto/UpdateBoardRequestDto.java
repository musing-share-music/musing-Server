package com.example.musing.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBoardRequestDto {

    private String title;
    private String musicTitle;
    private List<String> artist;
    private String youtubeLink;
    private List<String> hashtags;
    private List<Long> genre;
    private String content;
    private String playtime;
    private String songLink;
    private String albumName;
    private String thumbNailLink;
}
