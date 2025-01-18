package com.example.musing.board.dto;

import com.example.musing.board.entity.Board;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class BoardRequestDto {
    @Builder // 관리자 확인,
    public record BoardDto(
            String title,
            String musicName,
            String artistName,
            float rating,
            String username,
            LocalDateTime createAt,
            LocalDateTime updateAt,
            String youtubeLink,
            int replyCount,
            String thumbNailLink,
            String content,
            String imageUrl,
            List<String> genreList,
            List<String> moodList
    ) {
        public static BoardRequestDto.BoardDto toDto(Board board , List<String> genreList, List<String> moodList) {
            return BoardDto.builder()
                    .title(board.getTitle())
                    .musicName(board.getMusic().getName())
                    .artistName(board.getMusic().getArtist().getName())
                    .rating(board.getRating())
                    .username(board.getUser().getUsername())
                    .createAt(board.getCreatedAt())
                    .updateAt(board.getUpdatedAt())
                    .youtubeLink(board.getMusic().getSongLink())
                    .replyCount(board.getReplyCount())
                    .thumbNailLink(board.getMusic().getThumbNailLink())
                    .content(board.getContent())
                    .imageUrl(board.getImage())
                    .genreList(genreList)
                    .moodList(moodList)
                    .build();
        }
    }
}
