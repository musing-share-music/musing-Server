package com.example.musing.playlist.dto;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistRepresentativeDto {
    private String id;
    private String image;
    private String content;
}
