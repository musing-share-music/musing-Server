package com.example.musing.playlist.controller;


import com.example.musing.common.dto.ResponseDto;
import com.example.musing.common.utils.youtube.YouTubeUrlValidator;
import com.example.musing.exception.CustomException;
import com.example.musing.playlist.dto.*;
import com.example.musing.playlist.entity.PlayList;
import com.example.musing.playlist.repository.PlayListRepository;
import com.example.musing.playlist.service.PlaylistService;
import com.example.musing.user.entity.User;
import com.example.musing.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

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

    @PostMapping("/remove")
    public ResponseDto<String> modifyPlaylist(@RequestParam String playlistId)
            throws IOException, GeneralSecurityException, InterruptedException {
        playlistService.removePlaylist(playlistId);
        return ResponseDto.of(null, "플레이리스트를 삭제했습니다.");
    }

    @PutMapping("/modify")
    public ResponseDto<String> modifyPlaylist(@RequestBody @Valid YoutubePlaylistRequestDto playlistRequestDto,
                                              @RequestParam String playlistId,
                                              @RequestParam List<String> deleteVideoLinks) {
        playlistService.modifyPlaylist(playlistRequestDto, playlistId, deleteVideoLinks);
        return ResponseDto.of(null, "플레이리스트를 수정했습니다.");
    }

    @Operation(
            summary = "플레이리스트 저장",
            description = """
                사용자의 유튜브 플레이리스트 url을 받아 바로 저장하는 api입니다. PlaylistResponse Dto를 직접 보내주거나
                getUserPlaylist api를 호출하시면 해당 api에서 바로 저장가능한 api입니다.
                
                """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 저장 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식 또는 유효성 검사 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/savePlaylist")
    public ResponseDto<String> savePlaylist(
            @RequestBody @Valid PlaylistResponse playListDto) {
        playlistService.savePlayList(playListDto);
        return ResponseDto.of("OK", "플리저장성공");
    }

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


    @Operation(
            summary = "플레이리스트 불러오기 및 저장",
            description = "입력된 플레이리스트 URL을 통해 플레이리스트를 불러오고, 중복 여부를 확인하여 최대 3개까지 저장합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 플레이리스트를 받아왔습니다."),
            @ApiResponse(responseCode = "400", description = "플레이리스트를 불러오지 못했습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/getUserPlaylist")
    public ResponseDto<PlaylistResponse> getUserPlaylist(@RequestParam("url") String url) {
        PlaylistResponse playlistResponse = playlistService.getUserPlaylist(url);
        if(playlistResponse.getVideoList() == null || playlistResponse.getVideoList().isEmpty()) {
            return ResponseDto.of(null,"플레이리스트를 불러오지 못했습니다.");
        }
        return ResponseDto.of(playlistResponse,"성공적으로 플레이 리스트를 받아왔습니다.");
    }

    @Operation(
            summary = "내 플레이리스트 목록 조회",
            description = "현재 사용자가 보유한 플레이리스트를 최대 3개까지 조회합니다." +
                    "반환값은 플레이리스트 객체를 리스트 자료형으로 매핑한 형태로 반환됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 목록 가져오기 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/selectMyPlayLists")
    public ResponseDto<List<PlayList>> selectMyPlayLists() {
        SelectPlayListsDto dto = playlistService.selectMyPlayList();
        return ResponseDto.of(null, "플레이리스트 목록 가져오기 성공");
    }


}
