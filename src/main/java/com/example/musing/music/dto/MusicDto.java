package com.example.musing.music.dto;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.artist.entity.Artist_Music;
import com.example.musing.board.dto.BoardListResponseDto;
import com.example.musing.board.entity.Board;
import com.example.musing.music.entity.Music;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MusicDto(
        String musicName,
        List<ArtistDto> artists,
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

