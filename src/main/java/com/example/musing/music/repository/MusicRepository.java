
package com.example.musing.music.repository;
import com.example.musing.music.entity.Music;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicRepository extends JpaRepository<Music, Long> {


    Music findByMusicname(String username);
}