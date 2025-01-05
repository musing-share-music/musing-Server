package com.example.musing.music.entity;

import com.example.musing.artist.entity.Artist;
import com.example.musing.board.entity.Board;
import com.example.musing.hashtag.entity.HashTag;
import com.example.musing.like_music.entity.Like_Music;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter // Lombok 어노테이션 : 클래스 내 모든 필드의 Getter 메소드 자동 생성
@NoArgsConstructor // Lombok 어노테이션 : 기본 생성자 자동 추가
@Entity
@Table(name="music")
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String genre;

    @Column(nullable = false)
    private String playtime;

    @Column(nullable = true)
    private String albumName;

    @Column(nullable = false)
    private String songLink;

    @Column(nullable = false)
    private String thumbNailLink;

    // 음악과 게시판 일대다 관계 매핑
    @OneToMany(mappedBy = "music" , cascade = CascadeType.ALL, orphanRemoval = true )
    private List<Board> boardList = new ArrayList<Board>();

    // 음악과 해쉬태그 일대다 관계 매핑
    @OneToMany(mappedBy = "music" , cascade = CascadeType.ALL, orphanRemoval = true )
    private List<HashTag> HashTagList = new ArrayList<HashTag>();
    // 음악과 좋아요 일대다 관계 매핑
    @OneToMany(mappedBy = "music", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<Like_Music> preferMusics = new ArrayList<Like_Music>();


    //아티스트와 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist", nullable = false)
    private Artist artist;


    @Builder
    public Music(long id,String name,String artist,String genre,String playtime,String albumName){
        this.id = id;
        this.name = name;
        this.artist = Artist.builder().name(artist).build();
        this.genre = genre;
        this.playtime = playtime;
        this.albumName = albumName;

    }
}
