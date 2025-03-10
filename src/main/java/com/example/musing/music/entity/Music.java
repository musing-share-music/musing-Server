package com.example.musing.music.entity;

import com.example.musing.artist.entity.Artist_Music;
import com.example.musing.board.dto.CreateBoardRequest;
import com.example.musing.board.dto.UpdateBoardRequestDto;
import com.example.musing.board.entity.Board;
import com.example.musing.genre.entity.Genre_Music;
import com.example.musing.hashtag.entity.HashTag;
import com.example.musing.like_music.entity.Like_Music;
import com.example.musing.mood.entity.Mood_Music;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter // Lombok 어노테이션 : 클래스 내 모든 필드의 Getter 메소드 자동 생성
@NoArgsConstructor // Lombok 어노테이션 : 기본 생성자 자동 추가
@Entity
@Table(name = "music")
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "musicid")
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String playtime;

    @Column(nullable = true)
    private String albumName;

    @Column(nullable = false)
    private String songLink;

    @Column(nullable = false)
    private String thumbNailLink;

    @OneToMany(mappedBy = "music", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mood_Music> moodMusics = new ArrayList<>();

    @OneToMany(mappedBy = "music", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Genre_Music> genreMusics = new ArrayList<>();

    @OneToMany(mappedBy = "music", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Artist_Music> artists = new ArrayList<>();

    // 음악과 게시판 일대다 관계 매핑
    @OneToMany(mappedBy = "music", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boardList = new ArrayList<Board>();

    // 음악과 해쉬태그 일대다 관계 매핑
    @OneToMany(mappedBy = "music", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HashTag> hashTagList = new ArrayList<HashTag>();
    // 음악과 좋아요 일대다 관계 매핑
    @OneToMany(mappedBy = "music", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like_Music> preferMusics = new ArrayList<Like_Music>();

    //아티스트와 관계 매핑
    @Builder
    public Music(long id, String name, String playtime,
                 String albumName, String songLink, String thumbNailLink) {
        this.id = id;
        this.name = name;
        this.playtime = playtime;
        this.albumName = albumName;
        this.songLink = songLink;
        this.thumbNailLink = thumbNailLink;
    }

    public static Music of(CreateBoardRequest request) {
        return Music.builder()
                .name(request.getMusicTitle())
                .playtime(request.getPlaytime())
                .albumName(request.getAlbumName())
                .songLink(request.getSongLink())
                .thumbNailLink(request.getThumbNailLink())
                .build();
    }

    public Music updateMusic(UpdateBoardRequestDto request) {
        this.name = request.getMusicTitle();
        this.playtime = request.getPlaytime();
        this.songLink = request.getSongLink();
        this.albumName = request.getAlbumName();
        this.thumbNailLink = request.getThumbNailLink();
        return this;
    }

    // 해시태그 추가 메서드
    public void addHashTag(HashTag hashTag) {
        this.hashTagList.add(hashTag); // Music에 해시태그 추가
        hashTag.setMusic(this);       // HashTag에 Music 설정 (양방향 관계)
    }

    // 해시태그 삭제 메서드
    public void removeHashTag(HashTag hashTag) {
        this.hashTagList.remove(hashTag);
        hashTag.setMusic(null); // 관계 해제
    }

    // 해시태그 업데이트 메서드 (전체 변경)
    public void updateHashTags(List<HashTag> newHashTags) {
        // 기존 해시태그 제거
        this.hashTagList.clear();
        // 새로운 해시태그 추가
        newHashTags.forEach(this::addHashTag);
    }

}