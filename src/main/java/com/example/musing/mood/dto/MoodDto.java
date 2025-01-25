package com.example.musing.mood.dto;

import com.example.musing.mood.entity.Mood;
import com.example.musing.mood.entity.Mood_Music;
import lombok.Builder;

@Builder
public record MoodDto(
        long id,
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
