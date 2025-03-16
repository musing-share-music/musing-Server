package com.example.musing.playlist_music.entity;

import com.example.musing.music.entity.Music;
import com.example.musing.playlist.entity.PlayList;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "playlist_music")
public class PlaylistMusic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlistid", nullable = false)
    private PlayList playList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musicid", nullable = false)
    private Music music;

    @Builder
    public PlaylistMusic(PlayList playList, Music music) {
        this.playList = playList;
        this.music = music;
    }
}
