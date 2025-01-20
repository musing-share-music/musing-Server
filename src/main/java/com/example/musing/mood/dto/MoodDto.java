package com.example.musing.mood.dto;

import com.example.musing.mood.entity.Mood;
import com.example.musing.mood.entity.Mood_Music;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record MoodDto(
        @Schema(description = "분위기 ID", example = "1")
        long id,
        @Schema(description = "분위기명", example = "잔잔한")
        String moodName
) {
    public static MoodDto toDto(Mood_Music moodMusic) {
        return MoodDto.builder()
                .id(moodMusic.getId())
                .moodName(moodMusic.getMood().getMoodName().getKey())
                .build();
    }
    public static MoodDto toDto(Mood mood) {
        return MoodDto.builder()
                .id(mood.getId())
                .moodName(mood.getMoodName().getKey())
                .build();
    }
}
