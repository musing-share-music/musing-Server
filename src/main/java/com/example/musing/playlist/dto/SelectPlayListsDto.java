package com.example.musing.playlist.dto;

import jakarta.persistence.Column;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SelectPlayListsDto {

    private List<PlayListDto> playLists;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PlayListDto {
        private String listname;
        private Long itemCount;
        private String youtubePlaylistId;
        private String youtubeLink;
        private String description;
        private String thumbnailUrl;
    }
}