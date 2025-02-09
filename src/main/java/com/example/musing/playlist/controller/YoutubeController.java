package com.example.musing.playlist.controller;

import com.example.musing.common.YouTubeUrlValidator;
import com.example.musing.common.dto.ResponseDto;
import com.example.musing.music.repository.MusicRepository;
import com.example.musing.playlist.dto.YouTubeVideoResponse;
import com.example.musing.playlist.service.YoutubeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController("/musing/check")
public class YoutubeController {


    YoutubeService youTubeService;

    public YoutubeController(YoutubeService youtubeService) {
        this.youTubeService = youtubeService;

    }

    @Operation(summary = "음악 링크 등록 테스트용" ,
            description = "localhost:8090 기준 http://localhost:8090/youtube/search 로 접근<br>")



    @GetMapping("/checkURL")
    public String validateYouTubeUrl(@RequestParam String url) {
        return youTubeService.checkUrl(url);
    }


    @GetMapping("/getVideoInfo")
    public ResponseDto<YouTubeVideoResponse> getVideoInfo(@RequestParam("videoUrl") String videoUrl) {

        if (!YouTubeUrlValidator.isValidYouTubeUrl(videoUrl)) {
            return ResponseDto.of(null,"유효하지않은 URL입니다.");
        }

        // URL에서 Video ID 추출
        String videoId = youTubeService.checkUrl(videoUrl);

        //null값이면 알림 전송
        if (videoId == null) {
            return ResponseDto.of(null,"잘못된 URL입니다."); // 잘못된 URL 처리
        }

        // Video 정보 조회
        YouTubeVideoResponse videoResponse = youTubeService.getVideoInfo(videoId);

        return ResponseDto.of(videoResponse,"유효한 URL입니다.");
    }

//    @GetMapping("/validate-key")
//    public Map<String, Object> validateApiKey() {
//        boolean isValid = youTubeService.isApiKeyValid();
//        Map<String, Object> response = new HashMap<>();
//        response.put("valid", isValid);
//        response.put("message", isValid ? "✅ API 키가 유효합니다." : "❌ API 키가 유효하지 않습니다.");
//        return response;
//    }


}
