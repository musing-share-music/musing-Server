package com.example.musing.genre.repository;

import com.example.musing.genre.entity.Genre_Music;
import com.example.musing.music.entity.Music;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Genre_MusicRepository extends JpaRepository<Genre_Music, Long> {
    Optional<Genre_Music> findByMusic(Music music);
}
