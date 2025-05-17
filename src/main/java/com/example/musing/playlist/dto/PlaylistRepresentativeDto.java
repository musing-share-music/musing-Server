package com.example.musing.playlist.dto;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistRepresentativeDto {
    private String listName;    // 플레이리스트 이름
    private String description;      // 플레이리스트 설명
    private Long itemCount;          // 아이템 개수 (동영상 수)
    private String youtubePlaylistId;// 유튜브 플레이리스트 ID
    private String thumbnailUrl;      // 유튜브 링크 (대표 썸네일)
}
