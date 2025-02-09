package com.example.musing.artist.repository;

import com.example.musing.artist.entity.Artist_Music;
import com.example.musing.music.entity.Music;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Artist_MusicRepository extends JpaRepository<Artist_Music, Long> {
    Optional<Artist_Music> findByMusic(Music music);
    void deleteByMusic(Music music);
}
