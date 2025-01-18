
package com.example.musing.playlist.repository;
import com.example.musing.playlist.entity.PlayList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayListRepository extends JpaRepository<PlayList, Long> {
    boolean existsByYoutubePlaylistId(String youtubePlaylistId);
}