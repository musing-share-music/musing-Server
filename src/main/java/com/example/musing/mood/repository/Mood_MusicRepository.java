package com.example.musing.mood.repository;

import com.example.musing.mood.entity.Mood_Music;
import com.example.musing.music.entity.Music;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Mood_MusicRepository extends JpaRepository<Mood_Music, Long> {
    Optional<Mood_Music> findByMusic(Music music);
}
