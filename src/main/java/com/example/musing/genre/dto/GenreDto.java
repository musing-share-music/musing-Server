package com.example.musing.genre.dto;

import com.example.musing.genre.entity.Genre;
import com.example.musing.genre.entity.Genre_Music;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record GenreDto(
        @Schema(description = "장르 ID", example = "1")
        long id,
        @Schema(description = "장르명", example = "락")
        String genreName
) {
    public static GenreDto toDto(Genre_Music genreMusic){
        return GenreDto.builder()
                .id(genreMusic.getId())
                .genreName(genreMusic.getGenre().getGenreName().getKey())
                .build();
    }
    public static GenreDto toDto(Genre genre){
        return GenreDto.builder()
                .id(genre.getId())
                .genreName(genre.getGenreName().getKey())
                .build();
    }
}
