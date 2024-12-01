
package com.example.musing.repository.music;
import com.example.musing.entity.music.Music;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicRepository extends JpaRepository<Music, Long> {


    Music findByMusicname(String username);
}