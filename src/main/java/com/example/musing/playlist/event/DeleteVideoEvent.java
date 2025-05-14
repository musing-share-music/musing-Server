package com.example.musing.playlist.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class DeleteVideoEvent {
    private String playlistId;
    private List<String> deleteVideoIds;

    public static DeleteVideoEvent of(String playlistId, List<String> deleteVideoIds) {
        return DeleteVideoEvent.builder()
                .playlistId(playlistId)
                .deleteVideoIds(deleteVideoIds)
                .build();
    }
}
