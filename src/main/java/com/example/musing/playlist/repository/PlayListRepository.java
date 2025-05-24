
package com.example.musing.playlist.repository;
import com.example.musing.music.entity.Music;
import com.example.musing.playlist.entity.PlayList;
import com.example.musing.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayListRepository extends JpaRepository<PlayList, Long> {
    boolean existsByYoutubePlaylistIdAndUserId(String youtubePlaylistId,String userId);
    void deleteByYoutubePlaylistId(String playlistId);
    long countByUser(User user);
    List<PlayList> findByUser(User user);
    Optional<PlayList> findByYoutubePlaylistId(String youtubePlaylistId);
    Optional<PlayList> findByYoutubePlaylistIdAndUserId(String youtubePlaylistId,String userId);
    boolean existsByYoutubePlaylistId(String youtubePlaylistId);
    List<PlayList> findAllByUserId(String userId);
}
