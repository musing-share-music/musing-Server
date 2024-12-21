package com.example.musing.playlist.controller;

import com.example.musing.music.repository.MusicRepository;
import com.example.musing.playlist.service.YoutubeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class YoutubeController {


    YoutubeService youTubeService;

    public YoutubeController(YoutubeService youtubeService) {
        this.youTubeService = youtubeService;

    }

    // 검색키워드 가져와서 유튜브 api로 검색해 영상 가져오는 api
    @GetMapping("/youtube/search")
    public String search(@RequestParam String query) {
        return youTubeService.searchmusic(query);
    }



}
