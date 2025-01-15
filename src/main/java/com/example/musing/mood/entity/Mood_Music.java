package com.example.musing.mood.entity;

import com.example.musing.genre.entity.Genre;
import com.example.musing.genre.entity.Genre_Music;
import com.example.musing.music.entity.Music;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mood_music")
@Entity
public class Mood_Music {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musicid", nullable = false)
    private Music music;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moodid", nullable = false)
    private Mood mood;

    public Mood_Music of(Music music, Mood mood) {
        return Mood_Music.builder()
                .music(music)
                .mood(mood)
                .build();
    }
}
