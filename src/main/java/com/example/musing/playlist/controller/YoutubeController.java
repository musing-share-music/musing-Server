package com.example.musing.playlist.controller;

import com.example.musing.common.utils.youtube.YouTubeUrlValidator;
import com.example.musing.common.dto.ResponseDto;
import com.example.musing.playlist.dto.YouTubeVideoResponse;
import com.example.musing.playlist.service.YoutubeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

@RestController
public class YoutubeController {


    YoutubeService youTubeService;

    public YoutubeController(YoutubeService youtubeService) {
        this.youTubeService = youtubeService;

    }

    @Operation(summary = "음악 링크 등록 테스트용" ,
            description = "localhost:8090 기준 http://localhost:8090/youtube/search 로 접근<br>")

    //임시 보류 기능(검색어로 비디오 검색)
//    @GetMapping("/youtube/search")
//    public String search(@RequestParam("query") String query) {
//
//        return youTubeService.searchMusic(query);
//    }

    @PostMapping("/getVideoInfo")
    public ResponseDto<YouTubeVideoResponse> getVideoInfo(@RequestBody String videoUrl) {

        if (!YouTubeUrlValidator.isValidYouTubeUrl(videoUrl)) {
            return ResponseDto.of(null,"유효하지않은 URL입니다.");
        }

        // URL에서 Video ID 추출
        String videoId = youTubeService.extractVideoIdFromUrl(videoUrl);

        //null값이면 알림 전송
        if (videoId == null) {
            return ResponseDto.of(null,"잘못된 URL입니다."); // 잘못된 URL 처리
        }

        // Video 정보 조회
        YouTubeVideoResponse videoResponse = youTubeService.getVideoInfo(videoId);

        return ResponseDto.of(videoResponse,"유효한 URL입니다.");
    }


}
