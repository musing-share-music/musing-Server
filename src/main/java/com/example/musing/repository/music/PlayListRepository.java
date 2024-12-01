
package com.example.musing.repository.music;
import com.example.musing.entity.music.PlayList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayListRepository extends JpaRepository<PlayList, Long> {


    PlayList findByUsername(String username);
}