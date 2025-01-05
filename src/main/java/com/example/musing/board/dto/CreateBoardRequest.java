package com.example.musing.board.dto;


import lombok.Data;

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
    private String image; // Base64 인코딩된 이미지 데이터
    private String content;

}
