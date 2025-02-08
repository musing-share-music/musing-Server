package com.example.musing.utils;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class YoutubeService {

    private final RestClient restClient;

    public YoutubeService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://www.googleapis.com/youtube/v3")
                .build();
    }

    public String getYoutubeChannelId(String accessToken) {
        Map<String, Object> response = restClient.get()
                .uri("/channels?part=id&mine=true")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");

        if (items != null && !items.isEmpty()) {
            return (String) items.get(0).get("id");
        }

        return null;
    }
}
