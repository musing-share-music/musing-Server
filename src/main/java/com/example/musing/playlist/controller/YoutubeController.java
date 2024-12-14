package com.example.musing.playlist.controller;

import com.example.musing.playlist.service.YoutubeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class YoutubeController {

    private final YoutubeService youTubeService;

    public YoutubeController(YoutubeService youTubeService) {
        this.youTubeService = youTubeService;
    }

    @GetMapping("/youtube/search")
    public String search(@RequestParam String query) {
        return youTubeService.searchVideos(query);
    }
}
