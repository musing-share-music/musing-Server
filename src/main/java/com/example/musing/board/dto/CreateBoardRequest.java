package com.example.musing.board.dto;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateBoardRequest {
    private String userEmail;
    private String title;
    private String musicTitle;
    private String artist;
    private String youtubeLink;
    private List<String> hashtags;
    private String genre;
    private MultipartFile image;
    private String content;

}
