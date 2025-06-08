package com.example.musing.board.dto;


import com.example.musing.board.entity.Board;
import com.example.musing.board.entity.CheckRegister;
import lombok.Builder;
import lombok.Data;


import java.time.LocalDateTime;
import java.util.List;

@Builder
public record DetailResponse(
        String title,
        String musicTitle,
        String username,
        String email,
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
        String thumbNailLink,
        int viewCount,
        int likeCount,
        float rating,
        boolean isLike,
        String permitRegister) {
    public static DetailResponse of(Board board, List<String> artistsName, List<String> hashtags,
                                    List<String> genreName, boolean isLike) {
        return DetailResponse.builder()
                .title(board.getTitle())
                .musicTitle(board.getMusic().getName())
                .username(board.getUser().getUsername())
                .email(board.getUser().getEmail())
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
                .viewCount(board.getViewCount())
                .likeCount(board.getRecommendCount())
                .rating(board.getRating())
                .isLike(isLike)
                .permitRegister(board.getPermitRegister().name())
                .build();
    }
}
