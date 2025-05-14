package com.example.musing.playlist.event;

import com.example.musing.playlist.dto.YoutubePlaylistRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ModifyPlaylistEvent {
    private YoutubePlaylistRequestDto playlistRequestDto;
    private String playlistId;

    public static ModifyPlaylistEvent of(YoutubePlaylistRequestDto playlistRequestDto, String playlistId) {
        return ModifyPlaylistEvent.builder()
                .playlistRequestDto(playlistRequestDto)
                .playlistId(playlistId)
                .build();
    }
}
