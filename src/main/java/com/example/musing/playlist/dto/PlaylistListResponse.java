package com.example.musing.playlist.dto;


import com.example.musing.genre.dto.GenreDto;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistListResponse {

    private String name;            // 음악 이름 (곡 제목)
    private String playtime;        // 재생 시간
    private String songLink;        // 곡 URL
    private String thumbNailLink;   // 썸네일 URL
    private List<GenreDto> genres;  // 장르 정보 (필요 시)

}
