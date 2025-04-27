package com.example.musing.playlist.service;

import com.example.musing.exception.CustomException;
import com.example.musing.exception.ErrorCode;
import com.example.musing.music.entity.Music;
import com.example.musing.music.repository.MusicRepository;
import com.example.musing.playlist.dto.*;
import com.example.musing.playlist.entity.PlayList;
import com.example.musing.playlist.repository.PlayListRepository;
import com.example.musing.user.entity.User;
import com.example.musing.user.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



@RequiredArgsConstructor
@Service
public class PlaylistServiceImpl implements PlaylistService {

    private static final Logger logger = LoggerFactory.getLogger(PlaylistService.class);
    private String apiKey = "AIzaSyAc04gbKGheprJjcXPfnXu4l0tdBuzxowE";
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String YOUTUBE_PATTERN_STRING = "^(https?://)?(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/)[\\w-]{11}.*$";
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
        String playlistId = extractPlaylistId(url);
        if ("RDMM".equals(playlistId)) {
            logger.info("추천 재생목록(RDMM)은 API에서 지원되지 않습니다.");
            return null;
        }

        User user = getCurrentUser();

        JsonObject playlistInfo = fetchPlaylistInfo(playlistId);
        if (playlistInfo == null) {
            throw new CustomException(ErrorCode.FAILED_TO_FETCH_PLAYLIST);
        }

        // 💥 비공개 체크 추가
        String privacyStatus = playlistInfo.getAsJsonObject("status").get("privacyStatus").getAsString();
        if ("private".equalsIgnoreCase(privacyStatus)) {
            throw new CustomException(ErrorCode.PRIVATE_PLAYLIST);
            // ErrorCode에 PRIVATE_PLAYLIST 추가 필요
        }

        PlayList playList = new PlayList(
                "https://www.youtube.com/playlist?list=" + playlistId,
                playlistInfo.getAsJsonObject("snippet").get("title").getAsString(),
                (long) playlistInfo.getAsJsonArray("items").size(),
                playlistId,
                user
        );

        List<PlaylistListResponse> playlists = new ArrayList<>();
        PlaylistRepresentativeDto representative = null;

        // 페이징 로직으로 전체 영상 가져오기
        List<String> videoUrls = fetchAllPlaylistVideos(playlistId, playList,
                playlistInfo.getAsJsonObject("snippet").get("title").getAsString(),
                playlistInfo.getAsJsonObject("snippet").getAsJsonObject("thumbnails").getAsJsonObject("medium").get("url").getAsString()
        );

        // DTO 구성
        PlaylistListResponse listResponse = new PlaylistListResponse(
                playlistId,
                playlistInfo.getAsJsonObject("snippet").getAsJsonObject("thumbnails").getAsJsonObject("medium").get("url").getAsString(),
                playlistInfo.getAsJsonObject("snippet").get("title").getAsString(),
                playlistInfo.getAsJsonObject("snippet").get("channelTitle").getAsString(),
                new ArrayList<>(),
                videoUrls
        );
        playlists.add(listResponse);

        // 대표 영상
        if (!videoUrls.isEmpty()) {
            representative = new PlaylistRepresentativeDto(
                    listResponse.getTitle(),
                    listResponse.getThumbnailUrl(),
                    videoUrls.get(0)
            );
        }

        return new PlaylistResponse(playlists, representative);
    }


    @Transactional
    public PlayList savePlaylistWithMusic(PlaylistResponse playlistResponse, User user) {
        PlayList playList = PlayList.builder()
                .listname(playlistResponse.getRepresentative().getContent())
                .youtubePlaylistId(playlistResponse.getRepresentative().getId())
                .youtubeLink("https://www.youtube.com/playlist?list=" + playlistResponse.getRepresentative().getId())
                .itemCount((long) playlistResponse.getVideoList().size())
                .user(user)
                .build();

        playListRepository.save(playList);

        for (PlaylistListResponse playlistItem : playlistResponse.getVideoList()) {
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
        // RestTemplate 설정 (옵션으로 타임아웃, 에러 처리 추가)
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        RestTemplate restTemplate = new RestTemplate(factory);

        // 요청 바디 구성
        JSONObject body = createRequestBody(dto);

        // 요청 헤더 구성
        HttpHeaders headers = createHeaders(accessToken);

        // HttpEntity 구성 (헤더와 바디 포함)
        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

        try {
            // 유튜브 API 호출
            ResponseEntity<String> response = restTemplate.postForEntity(
                    API_BASE_URL + "/playlists?part=snippet,status",
                    entity,
                    String.class
            );

            // 응답 상태 체크 및 처리
            return handleResponse(response);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // 상태 코드와 응답 본문을 출력하여 에러 메시지 확인
            System.err.println("Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());

            // 권한 문제 (401 Unauthorized) 처리
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return "권한이 없습니다. 유효한 액세스 토큰을 확인하세요.";
            }

            // 그 외의 예외 처리
            throw new RuntimeException("Playlist creation failed: " + e.getMessage());
        }
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


    @Override
    public void deletePlaylistFromYouTube(String playlistId,String accessToken) throws IOException, GeneralSecurityException {
        // 1. accessToken을 이용해 인증된 YouTube 객체를 생성합니다.
        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);

        // 인증된 YouTube 객체 생성
        YouTube youtubeService = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        ).setApplicationName("musing").build();

        // 2. API 요청을 통해 플레이리스트 삭제
        YouTube.Playlists.Delete request = youtubeService.playlists().delete(playlistId);

        // 3. 실제 삭제 요청 실행
        try {
            request.execute();  // 삭제 요청을 유튜브에 보냄
        } catch (IOException e) {
            // 예외 처리 (예: 네트워크 문제, 권한 부족 등)
            throw new IOException("Failed to delete playlist from YouTube: " + e.getMessage(), e);
        }

    }

    private JSONObject createRequestBody(YoutubePlaylistRequestDto dto) {
        JSONObject body = new JSONObject();

        // snippet 객체 생성
        JSONObject snippet = new JSONObject();
        snippet.put("title", dto.getTitle()); // 플레이리스트 제목
        snippet.put("description", dto.getDescription()); // 플레이리스트 설명

        // status 객체 생성
        JSONObject status = new JSONObject();
        status.put("privacyStatus", "private"); // 기본은 비공개로 생성

        // 최종 body 구성
        body.put("snippet", snippet);
        body.put("status", status);

        return body;
    }

    private HttpHeaders createHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String handleResponse(ResponseEntity<String> response) {
        HttpStatus statusCode = (HttpStatus) response.getStatusCode();

        // 성공적인 응답 (200 OK)
        if (statusCode.is2xxSuccessful()) {
            // 성공 시에는 API에서 제공한 데이터를 반환
            return "✅ 유튜브 플레이리스트 생성 완료!";
        }

        // 오류 응답 처리
        if (statusCode.is4xxClientError()) {
            // 클라이언트 오류: 예를 들어, 잘못된 요청
            return "❌ 잘못된 요청입니다. 입력값을 확인해주세요.";
        } else if (statusCode.is5xxServerError()) {
            // 서버 오류: 유튜브 API 서버에서 발생한 문제
            return "❌ 유튜브 서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }

        // 기타 오류 응답
        return "❌ 예상치 못한 오류가 발생했습니다. 다시 시도해주세요.";
    }

    private List<String> fetchAllPlaylistVideos(String playlistId,
                                                PlayList playList,
                                                String playlistTitle,
                                                String thumbnailUrl) {
        List<String> videoUrls = new ArrayList<>();
        String nextPageToken = null;

        do {
            String url = API_BASE_URL + "/playlistItems?part=snippet"
                    + "&maxResults=50"
                    + "&playlistId=" + playlistId
                    + "&key=" + apiKey
                    + (nextPageToken != null ? "&pageToken=" + nextPageToken : "");

            JsonObject response = fetchJsonResponse(url);
            if (response == null) break;

            JsonArray items = response.getAsJsonArray("items");
            for (JsonElement videoItem : items) {
                JsonObject snippet = videoItem.getAsJsonObject().getAsJsonObject("snippet");
                JsonObject resourceId = snippet.getAsJsonObject("resourceId");
                if (resourceId != null && resourceId.has("videoId")) {
                    String videoId = resourceId.get("videoId").getAsString();
                    String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
                    videoUrls.add(videoUrl);

                    // 1. videoUrl로 이미 있는 Music 있는지 확인
                    Optional<Music> existingMusic = musicRepository.findBySongLink(videoUrl);

                    Music music;
                    if (existingMusic.isPresent()) {
                        music = existingMusic.get(); // 이미 있으면 가져오기
                    } else {
                        music = new Music(0, playlistTitle, "00:00", videoUrl, thumbnailUrl);
                        musicRepository.save(music); // 없으면 새로 저장
                    }

                    // 2. PlayList에 Music 추가
                    playList.addMusic(music);
                }
            }

            nextPageToken = response.has("nextPageToken") ?
                    response.get("nextPageToken").getAsString() : null;
        } while (nextPageToken != null);

        return videoUrls;
    }

    private JsonObject fetchJsonResponse(String url) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return JsonParser.parseString(response.getBody()).getAsJsonObject();
            } else {
                logger.error("YouTube API 호출 실패. Status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("YouTube API 요청 실패", e);
        }
        return null;
    }

    private User getCurrentUser() {
        return userRepository.findById(
                SecurityContextHolder.getContext().getAuthentication().getName()
        ).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    }

    private String extractPlaylistId(String url) {
        return URI.create(url).getQuery()
                .replaceFirst(".*list=", "");
    }
    private JsonObject fetchPlaylistInfo(String playlistId) {
        String url = "https://www.googleapis.com/youtube/v3/playlists?part=snippet&id=" + playlistId + "&key=" + apiKey;
        return fetchJsonResponse(url);
    }

}



