package com.example.musing.playlist.service;

import com.example.musing.common.dto.ResponseDto;
import com.example.musing.playlist.dto.*;
import com.example.musing.playlist.entity.PlayList;
import com.example.musing.user.entity.User;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;


public interface PlaylistService {
    void removePlaylist(String playlistId) throws IOException, GeneralSecurityException, InterruptedException;
    void modifyPlaylistInfo(String playlistId, List<String> videoIds) throws IOException, GeneralSecurityException, InterruptedException;
    YouTubeVideoResponse getVideoInfo(String videoId);

    String checkUrl(String url);

    public PlaylistResponse getUserPlaylists(String url);
    String getPlayTime(String url);
    String getThumailLink(String url);
    PlayList savePlaylistWithMusic(PlaylistResponse playlistResponse, User user);
    void savePlayList(PlayListSaveRequestDto playListDto);
    String createPlaylist(String accessToken, YoutubePlaylistRequestDto dto);
    String addVideoToPlaylist(String accessToken, YoutubeVideoRequestDto dto);
}
