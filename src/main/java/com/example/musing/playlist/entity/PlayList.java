package com.example.musing.playlist.entity;

import com.example.musing.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    //유저에 관한 외래키 보유 주인테이블
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    private User user; // 작성자

    @Builder
    public PlayList(String listname, Long itemCount, String youtubePlaylistId, User user) {
        this.listname = listname;
        this.itemCount = itemCount;
        this.youtubePlaylistId = youtubePlaylistId;
        this.user = user;
    }
}
