package com.example.musing.artist.dto;

import com.example.musing.artist.entity.Artist;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ArtistDto (
        @Schema(description = "아티스트 ID", example = "1")
        long id,
        @Schema(description = "아티스트명", example = "아티스트명")
        String name){
    public static ArtistDto toDto(Artist artist){
        return ArtistDto.builder()
                .id(artist.getId())
                .name(artist.getName())
                .build();
    }
}
