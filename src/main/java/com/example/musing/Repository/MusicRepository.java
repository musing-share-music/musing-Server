
package com.example.musing.Repository;
import com.example.musing.Entity.Music;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicRepository extends JpaRepository<Music, Long> {


    Music findByMusicname(String username);
}