package com.example.musing.playlist.event;

import com.example.musing.playlist.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class PlaylistEventListener {
    private final PlaylistService playlistService;

    @EventListener
    @Async
    public void deleteVideos(DeleteVideoEvent deleteVideoIds)
            throws GeneralSecurityException, IOException, InterruptedException {
        playlistService.removeVideoFromYoutubePlaylist(deleteVideoIds.getPlaylistId(),
                deleteVideoIds.getDeleteVideoIds());
    }

    @EventListener
    @Async
    public void modifyPlaylist(ModifyPlaylistEvent modifyPlaylistEvent)
            throws GeneralSecurityException, IOException, InterruptedException {
        playlistService.modifyYoutubePlaylistInfo(modifyPlaylistEvent.getPlaylistRequestDto(),
                modifyPlaylistEvent.getPlayList());
    }

    @EventListener
    @Async
    public void addMusicVideo(AddMusicVideoEvent addMusicVideoEvent) throws Exception {
        playlistService.addMusicToYoutubePlaylist
                (addMusicVideoEvent.getMusicUrl(),
                addMusicVideoEvent.getPlaylistId());
    }
}
