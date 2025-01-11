package com.example.musing.playlist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YouTubeVideoResponse {


    private String embedUrl;


    private String title;


    private String description;


    private String thumbnailUrl;


}