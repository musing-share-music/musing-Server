package com.example.musing.playlist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "플레이리스트 저장 요청 DTO")
public class PlayListSaveRequestDto {

    @Schema(description = "플레이리스트 이름", example = "취향저격 플레이리스트")
    private String listname;

    @Schema(description = "플레이리스트에 포함된 곡 개수", example = "5")
    private Long itemCount;

    @Schema(description = "유튜브 플레이리스트 ID", example = "PLytubeABC123")
    private String youtubePlaylistId;

    @Schema(description = "유튜브 플레이리스트 전체 링크", example = "https://youtube.com/playlist?list=PLytubeABC123")
    private String youtubeLink;

    @Schema(description = "플레이리스트에 담긴 음악 목록")
    private List<MusicDto> musicList;

    @Data
    @Schema(description = "음악 정보 DTO")
    public static class MusicDto {

        @Schema(description = "곡명", example = "봄날")
        private String name;

        @Schema(description = "재생 시간", example = "04:12")
        private String playtime;

        @Schema(description = "앨범명", example = "YOU NEVER WALK ALONE")
        private String albumName;

        @Schema(description = "곡 유튜브 링크", example = "https://youtube.com/watch?v=abcd1234")
        private String songLink;

        @Schema(description = "썸네일 이미지 링크", example = "https://img.youtube.com/vi/abcd1234/0.jpg")
        private String thumbNailLink;
    }
}