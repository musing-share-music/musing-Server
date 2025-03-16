package com.example.musing.playlist.service;

import com.example.musing.playlist.dto.*;
import com.example.musing.playlist.entity.PlayList;
import com.example.musing.user.entity.User;

import java.util.List;


public interface PlaylistService {

    YouTubeVideoResponse getVideoInfo(String videoId);

    String checkUrl(String url);

    public PlaylistResponse getUserPlaylists(String url);
    String getPlayTime(String url);
    String getThumailLink(String url);
    PlayList savePlaylistWithMusic(PlaylistResponse playlistResponse, User user);

    String createPlaylist(String accessToken, YoutubePlaylistRequestDto dto);
    String addVideoToPlaylist(String accessToken, YoutubeVideoRequestDto dto);
    String deleteVideoFromPlaylist(String accessToken, String playlistItemId);
}
