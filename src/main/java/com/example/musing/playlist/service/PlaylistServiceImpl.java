package com.example.musing.playlist.service;

import com.example.musing.exception.CustomException;
import com.example.musing.music.entity.Music;
import com.example.musing.music.repository.MusicRepository;
import com.example.musing.playlist.dto.*;
import com.example.musing.playlist.entity.PlayList;
import com.example.musing.playlist.repository.PlayListRepository;
import com.example.musing.user.entity.User;
import com.example.musing.user.repository.UserRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.musing.exception.ErrorCode.NOT_FOUND_USER;

@RequiredArgsConstructor
@Service
public class PlaylistServiceImpl implements PlaylistService {

    private static final Logger logger = Logger.getLogger(PlaylistService.class.getName());
    private String apiKey = "AIzaSyAc04gbKGheprJjcXPfnXu4l0tdBuzxowE";
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String YOUTUBE_PATTERN_STRING = "^(https?://)?(www\\.)?youtube\\.com/watch\\?v=[A-Za-z0-9_-]{11}$";
    private static final Pattern YOUTUBE_PATTERN = Pattern.compile(YOUTUBE_PATTERN_STRING);
    private static final String YOUTUBE_API_URL = "https://www.googleapis.com/youtube/v3/videos?id=%s&key=%s";

    private final String API_BASE_URL = "https://www.googleapis.com/youtube/v3";


    private final UserRepository userRepository;
    private final PlayListRepository playListRepository;
    private final MusicRepository musicRepository;



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

    public String getAlbumName(String url) {
        String VIDEO_ID = extractVideoId(url);
        String API_URL = "https://www.googleapis.com/youtube/v3/videos"
                + "?part=snippet"
                + "&id=" + VIDEO_ID
                + "&key=" + apiKey;


        return null;
    }

    public String getThumailLink(String url){
        String videoId = extractVideoId(url);
        return "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
    }

    public String checkUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "❌ URL이 비어 있습니다!";
        }

        // YouTube 영상 URL 패턴 검증
        Matcher matcher = YOUTUBE_PATTERN.matcher(url);
        if (!matcher.matches()) {
            return "❌ 유효한 YouTube 영상 URL이 아닙니다!";
        }

        String videoId = extractVideoId(url);
        String apiUrl = String.format(YOUTUBE_API_URL, videoId, apiKey);
        System.out.println("🔗 API 요청 URL: " + apiUrl);

        // RestTemplate을 이용한 API 요청
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

    // 영상 ID 추출 (URL에서 ?v= 뒤의 값을 추출)
    private String extractVideoId(String url) {
        String[] urlParts = url.split("v=");
        if (urlParts.length > 1) {
            return urlParts[1].split("&")[0];  // ?v=VIDEO_ID
        }
        return null;
    }

    private String extractPlaylistId(String url) {
        // URL에서 "list=" 파라미터를 기준으로 분리
        String[] urlParts = url.split("list=");
        if (urlParts.length > 1) {
            // "list=" 뒤의 값이 playlist ID입니다.
            return urlParts[1].split("&")[0];  // &로 구분된 경우에 대비
        }
        return null;  // playlist ID가 없는 경우 null 반환
    }

    public String getPlayTime(String youtubeUrl){

        String videoId = extractVideoId(youtubeUrl);
        if (videoId == null) {
            return "Invalid YouTube URL";
        }

        String apiUrl = "https://www.googleapis.com/youtube/v3/videos?part=contentDetails&id=" + videoId + "&key=" + apiKey;

        try {
            String response = restTemplate.getForObject(apiUrl, String.class);
            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
            String duration = jsonObject.getAsJsonArray("items")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("contentDetails")
                    .get("duration")
                    .getAsString();

            return convertDuration(duration);
        } catch (Exception e) {
            return "Error fetching video duration: " + e.getMessage();
        }
    }

    // ISO 8601 형식(PTHMS)을 HH:mm:ss 형식으로 변환
    private String convertDuration(String isoDuration) {
        return isoDuration.replace("PT", "")
                .replace("H", "h ")
                .replace("M", "m ")
                .replace("S", "s");
    }
    private User fetchUser() {
        // 예시로 현재 로그인한 사용자를 가져오는 로직
        // Spring Security나 다른 방법으로 사용자의 정보를 가져오는 로직을 구현해야 합니다.
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findById(userId).orElse(null);// 임시로 ID가 1인 사용자 반환
    }
    @Override
    public PlaylistResponse getUserPlaylists(String url) {
        String id = extractPlaylistId(url);
        List<PlaylistListResponse> playlists = new ArrayList<>();
        PlaylistRepresentativeDto representative = null;

        User user = userRepository.findById(SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        if (id.equals("RDMM")) {
            logger.info("추천 재생목록(RDMM)은 API에서 지원되지 않음.");
            return null;
        }

        try {
            String urlString = "https://www.googleapis.com/youtube/v3/playlists?part=snippet&id=" + id + "&key=" + apiKey;
            URL listUrl = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) listUrl.openConnection();
            connection.setRequestMethod("GET");

            int status = connection.getResponseCode();
            if (status != 200) {
                System.out.println("HTTP 요청 실패: " + status);
                return null;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonArray items = jsonResponse.getAsJsonArray("items");

            PlayList playList = new PlayList("https://www.youtube.com/playlist?list=" + id, "사용자 재생목록", (long) items.size(), id, user);

            for (JsonElement item : items) {
                JsonObject snippet = item.getAsJsonObject().getAsJsonObject("snippet");
                String playlistId = item.getAsJsonObject().get("id").getAsString();
                String title = snippet.get("title").getAsString();  // 타이틀
                String thumbnailUrl = snippet.getAsJsonObject("thumbnails").getAsJsonObject("medium").get("url").getAsString();  // 이미지 URL
                String channelTitle = snippet.get("channelTitle").getAsString();

                String image = thumbnailUrl; // 이미지 변수에 이미지 URL 할당
                // title 변수에는 제목을 그대로 넣어둡니다.

                List<String> videoUrls = new ArrayList<>();
                String playlistItemsUrlString = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=5&playlistId=" + playlistId + "&key=" + apiKey;
                URL playlistItemsUrl = new URL(playlistItemsUrlString);
                HttpURLConnection playlistItemsConnection = (HttpURLConnection) playlistItemsUrl.openConnection();
                playlistItemsConnection.setRequestMethod("GET");

                int itemsStatus = playlistItemsConnection.getResponseCode();
                if (itemsStatus == 200) {
                    BufferedReader playlistItemsIn = new BufferedReader(new InputStreamReader(playlistItemsConnection.getInputStream()));
                    StringBuilder playlistItemsResponse = new StringBuilder();
                    while ((inputLine = playlistItemsIn.readLine()) != null) {
                        playlistItemsResponse.append(inputLine);
                    }
                    playlistItemsIn.close();

                    JsonObject playlistItemsJsonResponse = JsonParser.parseString(playlistItemsResponse.toString()).getAsJsonObject();
                    JsonArray videoItems = playlistItemsJsonResponse.getAsJsonArray("items");

                    for (JsonElement videoItem : videoItems) {
                        JsonObject videoSnippet = videoItem.getAsJsonObject().getAsJsonObject("snippet");
                        JsonObject resourceId = videoSnippet.getAsJsonObject("resourceId");

                        if (resourceId != null && resourceId.has("videoId")) {
                            String videoId = resourceId.get("videoId").getAsString();
                            String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
                            videoUrls.add(videoUrl);

                            Music music = new Music(0, title, "00:00", videoUrl, thumbnailUrl);
                            musicRepository.save(music);
                            playList.addMusic(music);
                        }
                    }
                }

                PlaylistListResponse playlist = new PlaylistListResponse(playlistId,thumbnailUrl ,title , channelTitle, new ArrayList<>(), videoUrls);
                playlists.add(playlist);

                // Set the first video's details as the representative
                if (representative == null && !videoUrls.isEmpty()) {
                    representative = new PlaylistRepresentativeDto(title, image, videoUrls.get(0));  // 첫 번째 영상 정보를 representative에 설정
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new PlaylistResponse(playlists, representative);
    }


    @Transactional
    public PlayList savePlaylistWithMusic(PlaylistResponse playlistResponse, User user) {
        PlayList playList = PlayList.builder()
                .listname(playlistResponse.getRepresentative().getContent())
                .youtubePlaylistId(playlistResponse.getRepresentative().getId())
                .youtubeLink("https://www.youtube.com/playlist?list=" + playlistResponse.getRepresentative().getId())
                .itemCount((long) playlistResponse.getPlaylists().size())
                .user(user)
                .build();

        playListRepository.save(playList);

        for (PlaylistListResponse playlistItem : playlistResponse.getPlaylists()) {
            for (String videoUrl : playlistItem.getVideoUrls()) {
                Optional<Music> existingMusic = musicRepository.findBySongLink(videoUrl);
                if (existingMusic.isPresent()) {
                    playList.addMusic(existingMusic.get());
                }
            }
        }

        return playList;
    }

    public String createPlaylist(String accessToken, YoutubePlaylistRequestDto dto) {
        RestTemplate restTemplate = new RestTemplate();

        JSONObject body = new JSONObject();
        JSONObject snippet = new JSONObject();
        snippet.put("title", dto.getTitle());
        snippet.put("description", dto.getDescription());

        JSONObject status = new JSONObject();
        status.put("privacyStatus", "public");

        body.put("snippet", snippet);
        body.put("status", status);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                API_BASE_URL + "/playlists?part=snippet,status",
                entity,
                String.class
        );

        return response.getBody();
    }

    public String addVideoToPlaylist(String accessToken, YoutubeVideoRequestDto dto) {
        RestTemplate restTemplate = new RestTemplate();

        JSONObject snippet = new JSONObject();
        snippet.put("playlistId", dto.getPlaylistId());

        JSONObject resourceId = new JSONObject();
        resourceId.put("kind", "youtube#video");
        resourceId.put("videoId", dto.getVideoId());

        snippet.put("resourceId", resourceId);

        JSONObject body = new JSONObject();
        body.put("snippet", snippet);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                API_BASE_URL + "/playlistItems?part=snippet",
                entity,
                String.class
        );

        return response.getBody();
    }

    public String deleteVideoFromPlaylist(String accessToken, String playlistItemId) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                API_BASE_URL + "/playlistItems?id=" + playlistItemId,
                HttpMethod.DELETE,
                entity,
                String.class
        );

        return response.getStatusCode().is2xxSuccessful() ? "삭제 완료" : "삭제 실패";
    }
}



