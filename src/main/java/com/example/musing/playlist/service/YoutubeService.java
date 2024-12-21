package com.example.musing.playlist.service;

import com.example.musing.music.entity.Music;
import com.example.musing.music.repository.MusicRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class YoutubeService {


    private final RestTemplate restTemplate;
    private final MusicRepository repository;
    private final ObjectMapper objectMapper;

    public YoutubeService(RestTemplate restTemplate, MusicRepository repository, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Value("${youtube.api.key}")
    private String apiKey;

    private static final String API_URL = "https://www.googleapis.com/youtube/v3/";





    public String searchMusic(String query) {
        String url = API_URL + "search?part=snippet&query=" + query + "&key=" + apiKey;
        System.out.println("url :" +  url);
        String response = restTemplate.getForObject(url, String.class);
        System.out.println("response :" +  response);
        try {
            // JSON 파싱
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.get("items");

            // 각 아이템을 데이터베이스에 저장
            for (JsonNode item : items) {
                JsonNode snippet = item.get("snippet");
                //snippet 객체로 가져올수있는 정보 확인 필요
                // Music 객체를 빌더 패턴으로 생성
                Music music = Music.builder()
                        .name(snippet.get("title").asText())                       // 제목을 name으로 설정
                        .artist(snippet.get("channelTitle").asText())              // 채널 이름을 artist로 설정
                        .genre("Unknown")                                         // 기본 장르로 설정 (필요 시 수정)
                        .playtime("Unknown")                                      // 플레이타임 미제공 시 기본값 설정
                        .albumName(snippet.get("description").asText())           // 설명을 albumName으로 설정
                        .build();
                repository.save(music); // 데이터베이스에 저장
            }


        } catch (Exception e) {
            e.printStackTrace(); // 에러 처리
        }
        return restTemplate.getForObject(url, String.class);

    }
    
    
}