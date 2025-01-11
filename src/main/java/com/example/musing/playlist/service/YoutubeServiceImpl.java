package com.example.musing.playlist.service;

import com.example.musing.playlist.dto.YouTubeVideoResponse;
import com.google.api.client.util.Value;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class YoutubeServiceImpl implements YoutubeService {

    @Value("${youtube.api.key}")
    private String apiKey;

    @Override
    public YouTubeVideoResponse getVideoInfo(String videoId) {
        try {
            // YouTube API 요청 URL
            String apiUrl = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + videoId + "&key=" + apiKey;

            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();

            JsonObject snippet = response.getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonObject("snippet");
            String title = snippet.get("title").getAsString();
            String description = snippet.get("description").getAsString();
            String thumbnailUrl = snippet.getAsJsonObject("thumbnails").getAsJsonObject("high").get("url").getAsString();

            // 응답 DTO 생성
            YouTubeVideoResponse responseDto = new YouTubeVideoResponse();
            responseDto.setEmbedUrl("https://www.youtube.com/embed/" + videoId);
            responseDto.setTitle(title);
            responseDto.setDescription(description);
            responseDto.setThumbnailUrl(thumbnailUrl);

            return responseDto;
        } catch (Exception e) {
            throw new RuntimeException("YouTube API 요청 중 오류 발생", e);
        }
    }

    @Override
    public String extractVideoIdFromUrl(String url) {
        String videoId = null;
        if (url.contains("youtube.com")) {
            String[] urlParts = url.split("v=");
            if (urlParts.length > 1) {
                videoId = urlParts[1].split("&")[0];
            }
        } else if (url.contains("youtu.be")) {
            videoId = url.split("/")[url.split("/").length - 1];
        }
        return videoId;
    }
}
