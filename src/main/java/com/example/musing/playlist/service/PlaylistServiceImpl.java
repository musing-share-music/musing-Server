package com.example.musing.playlist.service;

import com.example.musing.auth.oauth2.service.Oauth2ProviderTokenService;
import com.example.musing.exception.CustomException;
import com.example.musing.exception.ErrorCode;
import com.example.musing.music.entity.Music;
import com.example.musing.music.repository.MusicRepository;
import com.example.musing.playlist.dto.*;
import com.example.musing.playlist.dto.PlaylistListResponse;
import com.example.musing.playlist.entity.PlayList;
import com.example.musing.playlist.event.DeleteVideoEvent;
import com.example.musing.playlist.event.ModifyPlaylistEvent;
import com.example.musing.playlist.repository.PlayListRepository;
import com.example.musing.playlist_music.entity.PlaylistMusic;
import com.example.musing.playlist_music.repository.PlayListMusicRepository;
import com.example.musing.user.entity.User;
import com.example.musing.user.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.musing.exception.ErrorCode.ERROR;


@RequiredArgsConstructor
@Service
public class PlaylistServiceImpl implements PlaylistService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PlayListSaveService saveService;

    private static final Logger logger = LoggerFactory.getLogger(PlaylistService.class);
    @Value("${youtube.api.key}")
    private String apiKey;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ApplicationEventPublisher publisher;

    private static final String YOUTUBE_PATTERN_STRING = "^(https?://)?(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/)[\\w-]{11}.*$";
    private static final Pattern YOUTUBE_PATTERN = Pattern.compile(YOUTUBE_PATTERN_STRING);
    private static final String YOUTUBE_API_URL = "https://www.googleapis.com/youtube/v3/videos?id=%s&key=%s";

    private final String API_BASE_URL = "https://www.googleapis.com/youtube/v3";

    private final UserRepository userRepository;
    private final PlayListRepository playListRepository;
    private final MusicRepository musicRepository;
    private final PlayListMusicRepository playlistMusicRepository;
    private final Oauth2ProviderTokenService oauth2ProviderTokenService;


    @Autowired
    public PlaylistServiceImpl(
            UserRepository userRepository,
            PlayListRepository playListRepository,
            MusicRepository musicRepository,
            PlayListMusicRepository playlistMusicRepository,
            Oauth2ProviderTokenService oauth2ProviderTokenService,
            ApplicationEventPublisher publisher

    ) {
        this.userRepository = userRepository;
        this.playListRepository = playListRepository;
        this.musicRepository = musicRepository;
        this.playlistMusicRepository = playlistMusicRepository;
        this.oauth2ProviderTokenService = oauth2ProviderTokenService;
        this.publisher = publisher;

    }

    @Transactional
    public void removePlaylist(String playlistId) {
        // 고아 객체를 이용하여 중간 매핑 테이블 삭제 처리
        deletePlaylistInDB(playlistId);
    }

    private void deletePlaylistInDB(String youtubePlId) {
        playListRepository.deleteByYoutubePlaylistId(youtubePlId);
    }

    @Override
    @Transactional
    public void modifyPlaylist(YoutubePlaylistRequestDto dto, String playlistId, List<String> deleteVideoLinks) {
        removeVideoFromDbPlaylist(deleteVideoLinks);
        publisher.publishEvent(DeleteVideoEvent.of(playlistId, deleteVideoLinks));
        publisher.publishEvent(ModifyPlaylistEvent.of(dto, playlistId));
    }

    private void removeVideoFromDbPlaylist(List<String> deleteVideoLinks) {
         playlistMusicRepository.deleteAllByMusic_SongLinkIn(deleteVideoLinks);
    }

    // 스프링 이벤트를 사용해서 트랜잭션 분리 및 비동기로 작업되도록 함
    @Override
    public void modifyYoutubePlaylistInfo(YoutubePlaylistRequestDto dto, String playlistId)
            throws IOException, InterruptedException, GeneralSecurityException {

        PlayList playlist = playListRepository.findByYoutubePlaylistId(playlistId)
                .orElseThrow(() -> new CustomException(ERROR));

        if(!playlist.getListname().equals(dto.getTitle()) ||
                !playlist.getDescription().equals(dto.getDescription())) {

            playlist.modifyTitleAndDescription(dto.getTitle(), dto.getDescription());

            // 1. 플레이리스트 아이템 목록 조회
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            String accessToken = oauth2ProviderTokenService.getGoogleProviderAccessToken(userId);
            GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);

            // 인증된 YouTube 객체 생성
            YouTube youtubeService = new YouTube.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    credential
            ).setApplicationName("musing").build();

            YouTube.Playlists.List getRequest = youtubeService.playlists()
                    .list(List.of("snippet"))
                    .setId(Collections.singletonList(playlistId));

            com.google.api.services.youtube.model.PlaylistListResponse getResponse = getRequest.execute();
            Playlist youtubePlaylist = getResponse.getItems().get(0);

            // snippet 객체 수정
            PlaylistSnippet snippet = youtubePlaylist.getSnippet();
            snippet.setTitle(dto.getTitle()); // 변경할 제목
            snippet.setDescription(dto.getDescription()); // 변경할 설명

            // 변경된 snippet을 playlist에 다시 세팅
            youtubePlaylist.setSnippet(snippet);

            // update 요청 실행
            YouTube.Playlists.Update updateRequest = youtubeService.playlists()
                    .update(List.of("snippet"), youtubePlaylist);
            Playlist updateResponse = updateRequest.execute();
        }
    }

    // 스프링 이벤트를 사용해서 트랜잭션 분리 및 비동기로 작업되도록 함
    // 유튜브 내의 플레이리스트 영상을 제외하는 메서드
    @Override
    public void removeVideoFromYoutubePlaylist(String playlistId, List<String> deleteVideoLinks)
            throws IOException, GeneralSecurityException, InterruptedException {
        // Youtube 실제 플레이리스트 수정 작업
        // 1. 플레이리스트 아이템 목록 조회
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        String accessToken = oauth2ProviderTokenService.getGoogleProviderAccessToken(userId);
        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);

        // 인증된 YouTube 객체 생성
        YouTube youtubeService = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        ).setApplicationName("musing").build();

        YouTube.PlaylistItems.List request = youtubeService.playlistItems()
                .list(Arrays.asList("id", "snippet"))
                .setPlaylistId(playlistId)
                .setMaxResults(20L);

        PlaylistItemListResponse response = request.execute();
        List<PlaylistItem> items = response.getItems();

        // 2. videoId와 매칭되는 playlistItemId 수집
        Map<String, String> videoIdMap = new HashMap<>();
        for (PlaylistItem item : items) {
            String itemVideoId = item.getSnippet().getResourceId().getVideoId();
            videoIdMap.put(itemVideoId, item.getId());
        }

        // 3. 각 비디오 ID별로 삭제 처리
        for (String videoLink: deleteVideoLinks) {
            String videoId = extractVideoId(videoLink);
            String playlistItemId = videoIdMap.get(videoId);

            if (playlistItemId == null) {
                continue;
            }

            // YouTube 삭제 요청
            youtubeService.playlistItems().delete(playlistItemId).execute();
        }
    }

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

    public String getThumbnailLink(String url){
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

            // 1. 먼저 "v=" 파라미터를 기준으로 시도
            String[] urlParts = url.split("v=");
            if (urlParts.length > 1) {
                return urlParts[1].split("&")[0]; // ?v=VIDEO_ID
            }

            // 2. "v="가 없으면 "list=" 파라미터를 기준으로 시도
            urlParts = url.split("list=");
            if (urlParts.length > 1) {
                return urlParts[1].split("&")[0]; // ?list=LIST_ID
            }

            // 3. 해당 파라미터가 없다면 null 반환
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

    // 🎵 영상 제목 가져오는 메서드
    public String getTitle(String youtubeUrl) {
        String videoId = extractVideoId(youtubeUrl);
        if (videoId == null) {
            return "Invalid YouTube URL";
        }

        String apiUrl = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + videoId + "&key=" + apiKey;

        try {
            String response = restTemplate.getForObject(apiUrl, String.class);
            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
            String title = jsonObject.getAsJsonArray("items")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("snippet")
                    .get("title")
                    .getAsString();
            if(title == null){
                return "Undefinded";
            }
            return title;
        } catch (Exception e) {
            return "Error fetching video title: " + e.getMessage();
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
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findById(userId).orElse(null);// 임시로 ID가 1인 사용자 반환
    }


    @Override
    public PlaylistResponse getUserPlaylist(String url) {
        // 1. 플레이리스트 ID 추출
        String playlistId = extractPlaylistId(url);
        if (playlistId == null || playlistId.isEmpty()) {
            return null; // 잘못된 playlistId일 경우 null 반환
        }

        logger.info("Extracted Playlist ID: " + playlistId);

        // 3. API에서 플레이리스트 정보 가져오기
        JsonObject playlistInfo = fetchPlaylistInfo(playlistId);
        if (playlistInfo == null) {
            return null; // 플레이리스트 정보를 가져올 수 없으면 null 반환
        }

        String title = getTitle(url);


        int videoCount = playlistInfo.getAsJsonArray("items") != null
                ? playlistInfo.getAsJsonArray("items").size()
                : 0;

        // 6. 비디오 URL 목록 가져오기
        List<String> videoUrls = fetchAllPlaylistVideos(playlistId);



        // 7. Video 정보로 PlaylistListResponse 생성
        List<PlaylistListResponse> videoList = new ArrayList<>();
        for (String videoUrl : videoUrls) {
            PlaylistListResponse videoResponse = PlaylistListResponse.builder()
                    .name(getTitle(videoUrl))                 // 영상 제목 (기본적으로 플레이리스트 제목 사용)
                    .songLink(videoUrl)
                    .playtime(getPlayTime(videoUrl))
                    .thumbNailLink(getThumbnailLink(videoUrl)) // 썸네일 URL (필요시 변경)
                    .genres(new ArrayList<>())   // 장르 (필요시 추가)
                    .build();
            videoList.add(videoResponse);
        }

        // 8. 대표 플레이리스트 정보 설정 (PlaylistRepresentativeDto)
        PlaylistRepresentativeDto representative = PlaylistRepresentativeDto.builder()
                .listName(getTitle(url))                        // 플레이리스트 이름
                .thumbnailUrl(getThumbnailLink(url)) // 대표 썸네일 (필요시 변경)
                .youtubePlaylistUrl(url)
                .youtubePlaylistId(playlistId)                        // 유튜브 플레이리스트 ID
                .build();

        // 9. DTO 구성
        PlaylistResponse dto = PlaylistResponse.builder()
                .videoList(videoList)
                .representative(representative)
                .build();

        // ✅ 프록시를 통해 트랜잭션 적용된 savePlayList 호출
        saveService.savePlayList(dto);

        // 10. 최종 응답 반환
        return dto;
    }

    @Transactional
    @Override
    public PlaylistResponse SelectMyDBPlaylist(String listId){

        User user = getCurrentUser();

        PlayList playlist = playListRepository.findByYoutubePlaylistIdAndUserId(listId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 플레이리스트가 존재하지 않습니다."));

        // PlaylistRepresentativeDto 생성
        PlaylistRepresentativeDto representativeDto = new PlaylistRepresentativeDto();
        representativeDto.setListName(playlist.getListname());
        representativeDto.setDescription(playlist.getDescription());
        representativeDto.setItemCount(playlist.getItemCount());
        representativeDto.setYoutubePlaylistId(playlist.getYoutubePlaylistId());
        representativeDto.setYoutubePlaylistUrl(playlist.getYoutubeLink());
        representativeDto.setThumbnailUrl(playlist.getThumbnail());

        Long playlistId = playlist.getId();

        List<PlaylistMusic> playlistMusicList = playlistMusicRepository.findByPlayListId(playlistId);

        // PlaylistListResponse 생성
        List<PlaylistListResponse> videoList = playlistMusicList.stream()
                .map(pm -> {
                    Music music = pm.getMusic();
                    return PlaylistListResponse.builder()
                            .name(music.getName())
                            .playtime(music.getPlaytime())
                            .songLink(music.getSongLink())
                            .thumbNailLink(music.getThumbNailLink())
                            .build();
                })
                .collect(Collectors.toList());

        // PlaylistResponse 반환
        PlaylistResponse playlistResponse = new PlaylistResponse();
        playlistResponse.setVideoList(videoList);
        playlistResponse.setRepresentative(representativeDto);

        return playlistResponse;
    }

    @Transactional
    @Override
    public List<PlaylistResponse> selectMyAllPlayListInfo(){
        User user = getCurrentUser();

        // 1. 사용자 ID로 모든 PlayList 조회
        List<PlayList> allPlaylists = playListRepository.findAllByUserId(user.getId());

        // 2. PlayList 각각에 대해 PlaylistResponse 생성
        return allPlaylists.stream().map(playlist -> {

            // 대표 정보 구성
            PlaylistRepresentativeDto representativeDto = PlaylistRepresentativeDto.builder()
                    .listName(playlist.getListname())
                    .description(playlist.getDescription())
                    .itemCount(playlist.getItemCount())
                    .youtubePlaylistId(playlist.getYoutubePlaylistId())
                    .youtubePlaylistUrl(playlist.getYoutubeLink())
                    .thumbnailUrl(playlist.getThumbnail())
                    .build();

            // 해당 playlistId에 해당하는 PlaylistMusic 조회
            List<PlaylistMusic> playlistMusicList = playlistMusicRepository.findByPlayListId(playlist.getId());

            // 음악 정보 구성
            List<PlaylistListResponse> videoList = playlistMusicList.stream()
                    .map(pm -> {
                        Music music = pm.getMusic();
                        return PlaylistListResponse.builder()
                                .name(music.getName())
                                .playtime(music.getPlaytime())
                                .songLink(music.getSongLink())
                                .thumbNailLink(music.getThumbNailLink())
                                .build();
                    })
                    .collect(Collectors.toList());

            // 하나의 PlaylistResponse 구성
            return PlaylistResponse.builder()
                    .representative(representativeDto)
                    .videoList(videoList)
                    .build();

        }).collect(Collectors.toList());

    }
    @Transactional
    @Override
    public void addNewPlaylist(String listName, String description)
            throws IOException, GeneralSecurityException, InterruptedException {

        // 현재 사용자 ID 가져오기
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = getCurrentUser();

        // 유저의 플레이리스트 개수 확인
        List<PlayList> userPlaylists = playListRepository.findAllByUser(user);

        // 조건 1: 최대 3개까지만 허용
        if (userPlaylists.size() >= 3) {
            throw new IllegalStateException("플레이리스트는 최대 3개까지만 생성할 수 있습니다.");
        }

        // 조건 2: 같은 이름의 플레이리스트가 이미 존재하는지 확인
        boolean nameExists = userPlaylists.stream()
                .anyMatch(p -> p.getListname().equalsIgnoreCase(listName));

        if (nameExists) {
            throw new IllegalStateException("이미 동일한 이름의 플레이리스트가 존재합니다.");
        }

        // Google OAuth2 AccessToken 가져오기
        String accessToken = oauth2ProviderTokenService.getGoogleProviderAccessToken(userId);
        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);

        // YouTube 서비스 생성
        YouTube youtubeService = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        ).setApplicationName("musing").build();

        // 유튜브용 snippet 생성
        PlaylistSnippet snippet = new PlaylistSnippet();
        snippet.setTitle(listName);
        snippet.setDescription(description);

        // playlistStatus 설정
        PlaylistStatus status = new PlaylistStatus();
        status.setPrivacyStatus("public");

        // playlist 객체 생성
        com.google.api.services.youtube.model.Playlist newPlaylist = new com.google.api.services.youtube.model.Playlist();
        newPlaylist.setSnippet(snippet);
        newPlaylist.setStatus(status);

        // 유튜브 API로 playlist 생성
        YouTube.Playlists.Insert insertRequest = youtubeService.playlists()
                .insert(List.of("snippet", "status"), newPlaylist);
        com.google.api.services.youtube.model.Playlist createdPlaylist = insertRequest.execute();

        String youtubePlaylistId = createdPlaylist.getId();
        String youtubeLink = "https://www.youtube.com/playlist?list=" + youtubePlaylistId;

        // DB 저장
        PlayList playlistEntity = PlayList.builder()
                .youtubePlaylistId(youtubePlaylistId)
                .listname(listName)
                .description(description)
                .itemCount(0L)
                .thumbnail("N/A")
                .youtubeLink(youtubeLink)
                .user(user)
                .build();

        playListRepository.save(playlistEntity);
    }

    @Transactional
    @Override
    public String addMusicToPlaylist(String url, String playlistId) {
        // 음악 조회
        Music music = musicRepository.findBySongLink(url)
                .orElseThrow(() -> new IllegalArgumentException("해당 음악 링크에 해당하는 음악 정보를 DB에서 찾을 수 없습니다."));

        // 플레이리스트 조회
        PlayList playList = playListRepository.findByYoutubePlaylistId(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("해당 플레이리스트를 찾을 수 없습니다."));

        // 이미 존재하는지 확인
        boolean exists = playlistMusicRepository.existsByPlayListIdAndMusicId(playList.getId(), music.getId());
        if (exists) {
            return "이미 해당 음악이 추가하려는 플레이리스트에 존재합니다.";
        }

        // 관계 저장
        PlaylistMusic playlistMusic = new PlaylistMusic(playList, music);
        playlistMusicRepository.save(playlistMusic); // save 명시적으로 호출

        // 카운트 증가 및 저장
        playList.setItemCount(playList.getItemCount() + 1);
        playListRepository.save(playList);

        return "음악이 플레이리스트에 성공적으로 추가되었습니다.";
    }


    @Override
    @Transactional
    public SelectPlayListsDto selectMyPlayList(){
        User user = getCurrentUser();

        // Optional로 플레이리스트 조회
        List<PlayList> playLists = playListRepository.findByUser(user);

        if(playLists.isEmpty()){
            return null;
        }


        List<SelectPlayListsDto.PlayListDto> dtoList = playLists.stream()
                .map(playList -> SelectPlayListsDto.PlayListDto.builder()
                        .listname(playList.getListname())
                        .itemCount(playList.getItemCount())
                        .youtubePlaylistId(playList.getYoutubePlaylistId())
                        .youtubeLink(playList.getYoutubeLink())
                        .description(playList.getDescription())
                        .thumbnailUrl(playList.getThumbnail())
                        .build())
                .toList();

        // SelectPlayListsDto 생성
        return SelectPlayListsDto.builder()
                .playLists(dtoList)
                .build();

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

    private List<String> fetchAllPlaylistVideos(String playlistId) {
        List<String> videoUrls = new ArrayList<>();
        String nextPageToken = null;

        do {
            // API URL 설정
            String url = API_BASE_URL + "/playlistItems?part=snippet"
                    + "&maxResults=20"
                    + "&playlistId=" + playlistId
                    + "&key=" + apiKey
                    + (nextPageToken != null ? "&pageToken=" + nextPageToken : "");

            // API 호출
            JsonObject response = fetchJsonResponse(url);
            if (response == null) {
                logger.error("API response is null for URL: " + url);
                break; // 응답이 null일 경우 루프 종료
            }

            // 'items' 배열 추출
            JsonArray items = response.getAsJsonArray("items");
            if (items == null || items.size() == 0) {
                logger.warn("No items found in the playlist response.");
                break; // 'items' 배열이 없거나 비어있으면 종료
            }

            // 'items' 배열을 순회하면서 비디오 URL 추출
            for (JsonElement videoItem : items) {
                JsonObject snippet = videoItem.getAsJsonObject().getAsJsonObject("snippet");
                if (snippet == null) {
                    logger.warn("Missing 'snippet' field in one of the video items.");
                    continue; // 'snippet'이 없으면 다음 아이템으로 넘어감
                }

                JsonObject resourceId = snippet.getAsJsonObject("resourceId");
                if (resourceId != null && resourceId.has("videoId")) {
                    String videoId = resourceId.get("videoId").getAsString();
                    String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
                    videoUrls.add(videoUrl); // 비디오 URL 추가
                } else {
                    logger.warn("No videoId found for video item.");
                }
            }

            // 다음 페이지가 있으면 페이지 토큰을 업데이트
            nextPageToken = response.has("nextPageToken") ? response.get("nextPageToken").getAsString() : null;
        } while (nextPageToken != null); // 페이지 토큰이 있으면 계속 반복

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
        Pattern pattern = Pattern.compile("[?&]list=([a-zA-Z0-9_-]+)");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            String playlistId = matcher.group(1);

            // 유효한 플레이리스트 ID인지 검증 (PL로 시작하고 길이가 충분한지)
            if (playlistId.startsWith("PL") && playlistId.length() >= 16) {
                return playlistId;
            } else {
                logger.warn("Invalid or auto-generated playlist ID: " + playlistId);
                return null;
            }
        }
        return null; // list 파라미터가 아예 없을 때
    }
    private JsonObject fetchPlaylistInfo(String playlistId) {
        String url = "https://www.googleapis.com/youtube/v3/playlists?part=snippet&id=" + playlistId + "&key=" + apiKey;
        return fetchJsonResponse(url);
    }

    private Boolean checkPlayList(String url){

        return playListRepository.existsByYoutubePlaylistId(url);
    }
    private static String extractAbChannel(String url) {
        if (url == null) return "Unknown";
        try {
            URL parsedUrl = new URL(url);
            String query = parsedUrl.getQuery();
            if (query == null) return "Unknown";

            String[] params = query.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=", 2);
                if (keyValue.length == 2 && keyValue[0].equals("ab_channel")) {
                    return keyValue[1];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }
}



