package com.example.musing.playlist.dto;


import com.example.musing.genre.dto.GenreDto;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistListResponse {
    private String id;
    private String thumbnailUrl;
    private String title;
    private String name;
    private List<GenreDto> genres;
    private List<String> videoUrls;

}
