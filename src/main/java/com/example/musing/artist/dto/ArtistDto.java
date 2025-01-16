package com.example.musing.artist.dto;

import com.example.musing.artist.entity.Artist;
import lombok.Builder;

@Builder
public record ArtistDto (
        long id,
        String name){
    public static ArtistDto toDto(Artist artist){
        return ArtistDto.builder()
                .id(artist.getId())
                .name(artist.getName())
                .build();
    }
}
