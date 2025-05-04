
package com.example.musing.playlist.repository;
import com.example.musing.music.entity.Music;
import com.example.musing.playlist.entity.PlayList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayListRepository extends JpaRepository<PlayList, Long> {
    boolean existsByYoutubePlaylistId(String youtubePlaylistId);
    void deleteByYoutubePlaylistId(String playlistId);



}
