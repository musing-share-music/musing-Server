
package com.example.musing.playlist_music.repository;

import com.example.musing.playlist.entity.PlayList;
import com.example.musing.playlist_music.entity.PlaylistMusic;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayListMusicRepository extends JpaRepository<PlaylistMusic, Long> {
    @EntityGraph(attributePaths = {"music"})
    void deleteAllByMusic_SongLinkIn(List<String> deleteVideoUrl);

    @Query("SELECT COUNT(pm) > 0 " +
            "FROM PlaylistMusic pm " +
            "JOIN pm.music m " +
            "JOIN pm.playList p " +
            "WHERE m.songLink = :songLink " +
            "AND p.youtubePlaylistId = :playlistId")
    boolean existsByMusicUrlAndPlaylistId(@Param("songLink") String songLink,
                                          @Param("playlistId") String playlistId);

    List<PlaylistMusic> findByPlayListId(Long playlistId);

    boolean existsByPlayListIdAndMusicId(Long playListId, Long musicId);

    @EntityGraph(attributePaths = {"music"})
    List<PlaylistMusic> findByPlayList(PlayList playList);
}
