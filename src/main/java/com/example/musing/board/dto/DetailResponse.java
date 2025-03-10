package com.example.musing.board.dto;


import com.example.musing.board.entity.Board;
import lombok.Builder;
import lombok.Data;


import java.time.LocalDateTime;
import java.util.List;

@Builder
public record DetailResponse(
    String title,
    String musicTitle,
    String username,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<String> artist,
    String youtubeLink,
    List<String> hashtags,
    List<String> genre,
    String content,
    String playtime,
    String AlbumName,
    String songLink,
    String thumbNailLink) {
    public static DetailResponse of(Board board, List<String> artistsName, List<String> hashtags, List<String> genreName) {
        return DetailResponse.builder()
                .title(board.getTitle())
                .musicTitle(board.getMusic().getName())
                .username(board.getUser().getUsername())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .artist(artistsName)
                .youtubeLink(board.getMusic().getSongLink())
                .hashtags(hashtags) // 해시태그 추출
                .genre(genreName)
                .content(board.getContent())
                .playtime(board.getMusic().getPlaytime())
                .AlbumName(board.getMusic().getAlbumName())
                .songLink(board.getMusic().getSongLink())
                .thumbNailLink(board.getMusic().getThumbNailLink())
                .build();
    }
}
