package com.example.musing.genre.dto;

import com.example.musing.genre.entity.Genre_Music;
import lombok.Builder;

@Builder
public record Genre_MusicDto(
        long id,
        String genreName
) {
    public static Genre_MusicDto toDto(Genre_Music genreMusic){
        return Genre_MusicDto.builder()
                .id(genreMusic.getId())
                .genreName(genreMusic.getGenre().getGenreName().getKey())
                .build();
    }
}
