package com.example.musing.playlist.event;

import com.example.musing.playlist.dto.YoutubePlaylistRequestDto;
import com.example.musing.playlist.entity.PlayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ModifyPlaylistEvent {
    private YoutubePlaylistRequestDto playlistRequestDto;
    private PlayList playList;

    public static ModifyPlaylistEvent of(YoutubePlaylistRequestDto playlistRequestDto, PlayList playList) {
        return ModifyPlaylistEvent.builder()
                .playlistRequestDto(playlistRequestDto)
                .playList(playList)
                .build();
    }
}
