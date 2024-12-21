package com.example.musing.playlist.controller;

import com.example.musing.music.repository.MusicRepository;
import com.example.musing.playlist.service.YoutubeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class YoutubeController {


    YoutubeService youTubeService;

    public YoutubeController(YoutubeService youtubeService) {
        this.youTubeService = youtubeService;

    }

    @Operation(summary = "음악 링크 등록 테스트용" ,
            description = "localhost:8090 기준 http://localhost:8090/youtube/search 로 접근<br>")

    @GetMapping("/youtube/search")
    public String search(@RequestParam("query") String query) {

        return youTubeService.searchMusic(query);
    }



}
