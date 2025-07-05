package com.example.musing.playlist.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class AddMusicVideoEvent {
    private String musicUrl;
    private String playlistId;

    public static AddMusicVideoEvent of(String musicUrl, String playlistId) {
        return AddMusicVideoEvent.builder()
                .musicUrl(musicUrl)
                .playlistId(playlistId)
                .build();
    }
}
