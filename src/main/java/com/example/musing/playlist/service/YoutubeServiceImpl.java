package com.example.musing.playlist.service;

import com.example.musing.playlist.dto.YouTubeVideoResponse;
import com.example.musing.playlist.entity.PlayList;
import com.google.api.client.util.Value;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class YoutubeServiceImpl implements YoutubeService {


    private String apiKey = "AIzaSyAc04gbKGheprJjcXPfnXu4l0tdBuzxowE";


    private static final String YOUTUBE_API_URL = "https://www.googleapis.com/youtube/v3/videos?part=status&id=%s&key=%s";

    private static final String TEST_API_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=테스트&key=";
    private static final String YOUTUBE_REGEX = "^(https?:\\/\\/)?(www\\.)?(youtube\\.com\\/watch\\?v=|youtu\\.be\\/)([A-Za-z0-9_-]{11})(.*)?$";
    private static final Pattern YOUTUBE_PATTERN = Pattern.compile(YOUTUBE_REGEX);


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
    public String checkUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "❌ URL이 비어 있습니다!";
        }

        Matcher matcher = YOUTUBE_PATTERN.matcher(url);
        if (!matcher.find()) { // matches() 대신 find() 사용
            return "❌ 유효한 YouTube URL이 아닙니다!";
        }

        String videoId = extractVideoId(url);
        String apiUrl = String.format(YOUTUBE_API_URL, videoId, apiKey);
        System.out.println("🔗 API 요청 URL: " + apiUrl);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            return "❌ 유튜브 API 요청 실패!";
        }

        String responseBody = response.getBody();
        if (responseBody.contains("\"privacyStatus\": \"public\"")) {
            return "✅ 이 영상은 공개 상태입니다!";
        } else if (responseBody.contains("\"privacyStatus\": \"unlisted\"")) {
            return "⚠️ 이 영상은 미등록 상태입니다!";
        } else if (responseBody.contains("\"privacyStatus\": \"private\"")) {
            return "🔒 이 영상은 비공개 상태입니다!";
        }

        return "❌ 영상 정보를 가져올 수 없습니다!";


    }


    @Override
    public List<PlayList> getUserPlaylists(String id) {
        try {
            // YouTube API 요청 URL
            String apiUrl = "https://www.googleapis.com/youtube/v3/playlists?part=snippet,contentDetails&mine=true&maxResults=10";

            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + id); // Access Token 설정

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();

            List<PlayList> playlists = new ArrayList<>();
            response.getAsJsonArray("items").forEach(item -> {
                JsonObject playlistObject = item.getAsJsonObject();
                JsonObject snippet = playlistObject.getAsJsonObject("snippet");

                String playlistId = playlistObject.get("id").getAsString();
                String title = snippet.get("title").getAsString();
                Long itemCount = playlistObject.getAsJsonObject("contentDetails").get("itemCount").getAsLong();

                PlayList playList = PlayList.builder()
                        .listname(title)
                        .itemCount(itemCount)
                        .youtubePlaylistId(playlistId)
                        .build();

                playlists.add(playList);
            });

            return playlists;
        } catch (Exception e) {
            throw new RuntimeException("YouTube API 요청 중 오류 발생", e);
        }

    }
    private String extractVideoId(String url) {
        String pattern = "^(?:https?:\\/\\/)?(?:www\\.)?(?:youtube\\.com\\/watch\\?v=|youtu\\.be\\/)([A-Za-z0-9_-]{11}).*$";
        return url.matches(pattern) ? url.replaceAll(pattern, "$1") : null;
    }

    public boolean isApiKeyValid() {
        String url = TEST_API_URL + apiKey;
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getBody() != null && !response.getBody().containsKey("error")) {
                return true; // 🔹 API 키가 유효함
            }
        } catch (Exception e) {
            return false; // 🔹 API 키가 유효하지 않음
        }
        return false;
    }

}