package com.example.musing.playlist.entity;

import com.example.musing.music.entity.Music;
import com.example.musing.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.example.musing.playlist_music.entity.PlaylistMusic;

import java.util.ArrayList;
import java.util.List;

@Getter // Lombok 어노테이션 : 클래스 내 모든 필드의 Getter 메소드 자동 생성
@NoArgsConstructor // Lombok 어노테이션 : 기본 생성자 자동 추가
@Entity
@Table(name="playlist")
public class PlayList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="playlistid")
    private long id;

    @Column(nullable = false)
    private String listname;

    @Column(nullable = false)
    private Long itemCount;

    @Column(nullable = false)
    private String youtubePlaylistId;

    @Column(nullable = false)
    private String youtubeLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    private User user; // 작성자

    @OneToMany(mappedBy = "playList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaylistMusic> playlistMusicList = new ArrayList<>();

    @Builder
    public PlayList(String youtubeLink, String listname, Long itemCount, String youtubePlaylistId, User user) {
        this.listname = listname;
        this.itemCount = itemCount;
        this.youtubePlaylistId = youtubePlaylistId;
        this.youtubeLink = youtubeLink;
        this.user = user;
    }

    // 🎯 플레이리스트에 음악 추가
    public void addMusic(Music music) {
        PlaylistMusic playlistMusic = new PlaylistMusic(this, music);
        this.playlistMusicList.add(playlistMusic);
    }
}
