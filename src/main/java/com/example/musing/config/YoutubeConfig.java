package com.example.musing.config;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YoutubeConfig {

    @Bean
    public YouTube youtube() {
        JsonFactory jsonFactory = new GsonFactory();
        return new YouTube.Builder(
                new NetHttpTransport(),
                jsonFactory,
                request -> {}
        ).build();
    }
}