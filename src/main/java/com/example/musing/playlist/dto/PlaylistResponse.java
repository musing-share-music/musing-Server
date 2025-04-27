package com.example.musing.playlist.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistResponse {
    private List<PlaylistListResponse> videoList;  // 플레이리스트 목록
    private PlaylistRepresentativeDto representative;
}
