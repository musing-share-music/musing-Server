package com.example.musing.playlist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class YoutubeValidationResponse {
    private String videoId;
    private String message;
    private boolean valid;
}
