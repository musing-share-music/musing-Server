
package com.example.musing.playlist_music.repository;

import com.example.musing.playlist.entity.PlayList;
import com.example.musing.playlist_music.entity.PlaylistMusic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayListMusicRepository extends JpaRepository<PlaylistMusic, Long> {

    public Boolean findByplaylistu(Long playlistId);
    public List<PlayList> findByPlaylistId(Long playlistId);

}
