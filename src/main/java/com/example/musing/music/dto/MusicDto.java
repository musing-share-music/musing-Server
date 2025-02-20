package com.example.musing.music.dto;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.artist.entity.Artist_Music;
import com.example.musing.board.dto.BoardListResponseDto;
import com.example.musing.board.entity.Board;
import com.example.musing.music.entity.Music;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MusicDto(
        @Schema(description = "노래 제목", example = "Manhattan")
        String musicName,
        @Schema(description = "가수 목록")
        List<ArtistDto> artists,
        @Schema(description = "해당 곡의 유튜브 썸네일 링크")
        String thumbNailLink
) {
    public static MusicDto toDto(Music music) {
        return MusicDto.builder()
                .musicName(music.getName())
                .artists(music.getArtists().stream()
                        .map(Artist_Music::getArtist).toList()
                        .stream().map(ArtistDto::toDto).toList()
                )
                .thumbNailLink(music.getThumbNailLink())
                .build();
    }
}

