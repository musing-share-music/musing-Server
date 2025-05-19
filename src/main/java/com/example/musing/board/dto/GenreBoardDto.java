package com.example.musing.board.dto;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.artist.entity.Artist_Music;
import com.example.musing.board.entity.Board;
import com.example.musing.music.entity.Music;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
public record GenreBoardDto(
        @Schema(description = "장르관련 음악 추천 게시판 고유 ID", example = "21")
        long id,
        @Schema(description = "노래 제목", example = "Back In Black")
        String musicName,
        @Schema(description = "아티스트 Id 및 이름")
        List<ArtistDto> artists,
        @Schema(description = "유튜브 썸네일 사진 링크", example = "https://img.youtube.com/vi/pAgnJDJN4VA/maxresdefault.jpg")
        String thumbNailLink,
        @Schema(description = "유튜브 링크", example = "https://www.youtube.com/watch?v=gdZLi9oWNZg")
        String musicLink) {
    public static GenreBoardDto toDto(Board board){
        return GenreBoardDto.builder()
                .id(board.getId())
                .musicName(board.getMusic().getName())
                .artists(board.getMusic().getArtists().stream()
                        .map(Artist_Music::getArtist)
                        .map(ArtistDto::toDto).toList())
                .thumbNailLink(board.getMusic().getThumbNailLink())
                .musicLink(board.getMusic().getSongLink())
                .build();
    }
}
