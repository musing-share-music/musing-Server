package com.example.musing.mood.dto;

import com.example.musing.mood.entity.Mood_Music;
import lombok.Builder;

@Builder
public record Mood_MusicDto(
        long id,
        String moodName
) {
    public static Mood_MusicDto toDto(Mood_Music moodMusic) {
        return Mood_MusicDto.builder()
                .id(moodMusic.getId())
                .moodName(moodMusic.getMood().getMoodName().getKey())
                .build();
    }
}
