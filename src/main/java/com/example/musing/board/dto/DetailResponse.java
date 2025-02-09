package com.example.musing.board.dto;


import lombok.Builder;
import lombok.Data;


import java.util.List;

@Builder
@Data
public class DetailResponse {

    private String title;
    private String musicTitle;
    private String artist;
    private String youtubeLink;
    private List<String> hashtags;
    private Long genre;
    private String content;
    private String playtime;
    private String AlbumName;
    private String songLink;
    private String thumbNailLink;


}
