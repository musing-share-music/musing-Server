package com.example.musing.playlist.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class YoutubeService {
    @Value("${youtube.api.key}")
    private String apiKey;

    private static final String API_URL = "https://www.googleapis.com/youtube/v3/";

    private final RestTemplate restTemplate;


    public YoutubeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public String searchVideos(String query) {
        String url = API_URL + "search?part=snippet&q=" + query + "&key=" + apiKey;
        return restTemplate.getForObject(url, String.class);
    }
}