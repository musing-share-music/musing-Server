package com.example.musing.playlist.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YoutubeVideoRequestDto {
    private String playlistId;
    private String videoId;
    private String playlistItemId; // 삭제 시 사용
}