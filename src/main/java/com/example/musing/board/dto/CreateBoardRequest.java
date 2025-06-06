package com.example.musing.board.dto;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

//클라이언트로부터 전달 받은 데이터 객체화
@Data
public class CreateBoardRequest {


    private String title;
    private String musicTitle;
    private List<String> artist;
    private String youtubeLink;
    private List<String> hashtags;
    private List<Long> genre;
    private String content;
    private String playtime;
    private String AlbumName;
    private String thumbNailLink;
}
