package com.example.musing.playlist.controller;

import com.example.musing.common.utils.youtube.YouTubeUrlValidator;
import com.example.musing.common.dto.ResponseDto;
import com.example.musing.playlist.dto.YouTubeVideoResponse;
import com.example.musing.playlist.service.PlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

@RestController("/musing/check")
public class YoutubeController {


    PlaylistService youTubeService;


//    @GetMapping("/validate-key")
//    public Map<String, Object> validateApiKey() {
//        boolean isValid = youTubeService.isApiKeyValid();
//        Map<String, Object> response = new HashMap<>();
//        response.put("valid", isValid);
//        response.put("message", isValid ? "✅ API 키가 유효합니다." : "❌ API 키가 유효하지 않습니다.");
//        return response;
//    }


}
