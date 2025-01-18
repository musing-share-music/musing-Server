package com.example.musing.playlist.service;

import com.example.musing.playlist.dto.YouTubeVideoResponse;
import com.example.musing.playlist.entity.PlayList;

import java.util.List;


public interface YoutubeService {

    YouTubeVideoResponse getVideoInfo(String videoId);

    String extractVideoIdFromUrl(String url);

    List<PlayList> getUserPlaylists(String accessToken);
}
