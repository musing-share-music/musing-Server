package com.example.musing.playlist.service;

import com.example.musing.playlist.dto.YouTubeVideoResponse;


public interface YoutubeService {

    YouTubeVideoResponse getVideoInfo(String videoId);

    String extractVideoIdFromUrl(String url);

}
