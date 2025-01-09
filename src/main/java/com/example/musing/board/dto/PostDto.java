package com.example.musing.board.dto;

import com.example.musing.board.entity.Board;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private String musicTitle;
    private String artist;
    private String youtubeLink;
    private List<String> hashtags;
    private String genre;

    public static PostDto fromEntity(Board board) {
        return PostDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .musicTitle(board.getMusic().getName())
                .artist(board.getMusic().getArtist().getName())
                .youtubeLink(board.getYoutubeLink())
                .genre(board.getMusic().getGenre())
                .build();
    }
}
