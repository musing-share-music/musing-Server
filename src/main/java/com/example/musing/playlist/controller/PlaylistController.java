package com.example.musing.playlist.controller;


import com.example.musing.common.dto.ResponseDto;
import com.example.musing.common.utils.youtube.YouTubeUrlValidator;
import com.example.musing.exception.CustomException;
import com.example.musing.playlist.dto.PlaylistResponse;
import com.example.musing.playlist.dto.YouTubeVideoResponse;
import com.example.musing.playlist.dto.YoutubePlaylistRequestDto;
import com.example.musing.playlist.dto.YoutubeVideoRequestDto;
import com.example.musing.playlist.entity.PlayList;
import com.example.musing.playlist.repository.PlayListRepository;
import com.example.musing.playlist.service.PlaylistService;
import com.example.musing.user.entity.User;
import com.example.musing.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static com.example.musing.exception.ErrorCode.NOT_FOUND_USER;

@RestController
@RequestMapping("/musing/playlist")
public class PlaylistController {

    private final PlaylistService playlistService;
    private final PlayListRepository playListRepository;
    private final UserRepository userRepository;

    public PlaylistController(UserRepository userRepository,PlaylistService playlistService, PlayListRepository playListRepository) {
        this.playlistService = playlistService;
        this.playListRepository = playListRepository;
        this.userRepository = userRepository;
    }


//    @GetMapping("/getMyPlaylists")
//    public ResponseDto<List<PlayList>> getMyPlaylists(@RequestParam("id") String id) {
//
//
//        // YouTube API 호출 및 재생목록 가져오기
////        List<PlayList> playlists = playlistService.getUserPlaylists(id);
//
//        // 데이터베이스에 저장
//        playlists.forEach(playList -> {
//            if (!playListRepository.existsByYoutubePlaylistId(playList.getYoutubePlaylistId())) {
//                playListRepository.save(playList);
//            }
//        });
//
//        return ResponseDto.of(playlists, "성공적으로 유저 재생목록을 불러왔습니다.");
//    }


    @Operation(summary = "음악 링크 등록 테스트용" ,
            description = "localhost:8090 기준 http://localhost:8090/youtube/search 로 접근<br>")



    @GetMapping("/checkURL")
    public String validateYouTubeUrl(@RequestParam String url) {
        return playlistService.checkUrl(url);
    }


    @GetMapping("/testGetPlayTime")
    public String testGetPlayTime(@RequestParam String url) {

        return playlistService.getPlayTime(url);

    }

    @PostMapping("/playlist")
    public String createPlaylist(@RequestHeader("Authorization") String authHeader,
                                 @RequestBody YoutubePlaylistRequestDto dto) {
        String accessToken = authHeader.replace("Bearer ", "");
        return playlistService.createPlaylist(accessToken, dto);
    }

    @PostMapping("/video")
    public String addVideoToPlaylist(@RequestHeader("Authorization") String authHeader,
                                     @RequestBody YoutubeVideoRequestDto dto) {
        String accessToken = authHeader.replace("Bearer ", "");
        return playlistService.addVideoToPlaylist(accessToken, dto);
    }

    @GetMapping("/getVideoInfo")
    public ResponseDto<YouTubeVideoResponse> getVideoInfo(@RequestParam("videoUrl") String videoUrl) {

        if (!YouTubeUrlValidator.isValidYouTubeUrl(videoUrl)) {
            return ResponseDto.of(null,"유효하지않은 URL입니다.");
        }

        // URL에서 Video ID 추출
        String videoId = playlistService.checkUrl(videoUrl);

        //null값이면 알림 전송
        if (videoId == null) {
            return ResponseDto.of(null,"잘못된 URL입니다."); // 잘못된 URL 처리
        }

        // Video 정보 조회
        YouTubeVideoResponse videoResponse = playlistService.getVideoInfo(videoId);

        return ResponseDto.of(videoResponse,"유효한 URL입니다.");
    }

    @GetMapping("/getUserPlaylists")
    public ResponseDto<PlaylistResponse> getUserPlaylists(@RequestParam("url") String url) {
        PlaylistResponse playlistResponse = playlistService.getUserPlaylists(url);
        if(playlistResponse.getPlaylists() == null || playlistResponse.getPlaylists().isEmpty()) {
            return ResponseDto.of(null,"플레이리스트를 불러오지 못했습니다.");
        }
        return ResponseDto.of(playlistResponse,"성공적으로 플레이 리스트를 받아왔습니다.");
    }

    @PostMapping("/save-playlist")
    public ResponseDto<PlayList> savePlaylist(@RequestBody PlaylistResponse playlistResponse) {

        User user = userRepository.findById(SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName()).orElseThrow(() -> new CustomException(NOT_FOUND_USER)); // 현재 로그인한 유저 가져오기
        PlayList savedPlaylist = playlistService.savePlaylistWithMusic(playlistResponse, user);

        return ResponseDto.of(savedPlaylist,"성공적으로 저장되었습니다.");
    }

    @DeleteMapping("/delete")
    public ResponseDto<String> deletePlaylist(
            @RequestParam("playlistId") String playlistId,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String accessToken = authHeader.replace("Bearer ", "");

            // 유튜브에서 삭제
            playlistService.deletePlaylistFromYouTube(playlistId, accessToken);

            // DB에서도 삭제
            playListRepository.deleteByYoutubePlaylistId(playlistId);

            return ResponseDto.of("삭제 완료", "플레이리스트가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseDto.of(null, "삭제 실패: " + e.getMessage());
        }
    }

}
