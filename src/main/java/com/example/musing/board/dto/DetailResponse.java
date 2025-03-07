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
    String artist,
    String youtubeLink,
    List<String> hashtags,
    Long genre,
    String content,
    String playtime,
    String AlbumName,
    String songLink,
    String thumbNailLink) {
    public static DetailResponse of(Board board, String artistsName, List<String> hashtags, Long genreIds) {
        return DetailResponse.builder()
                .title(board.getTitle())
                .musicTitle(board.getMusic().getName())
                .artist(artistsName)
                .youtubeLink(board.getMusic().getSongLink())
                .hashtags(hashtags) // 해시태그 추출
                .genre(genreIds)
                .content(board.getContent())
                .playtime(board.getMusic().getPlaytime())
                .AlbumName(board.getMusic().getAlbumName())
                .songLink(board.getMusic().getSongLink())
                .thumbNailLink(board.getImage())
                .build();
    }
}
