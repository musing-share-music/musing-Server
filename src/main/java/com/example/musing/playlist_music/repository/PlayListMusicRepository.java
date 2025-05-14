
package com.example.musing.playlist_music.repository;

import com.example.musing.playlist_music.entity.PlaylistMusic;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayListMusicRepository extends JpaRepository<PlaylistMusic, Long> {
    @EntityGraph("music")
    void deleteAllByMusic_SongLinkIn(List<String> deleteVideoUrl);
}
