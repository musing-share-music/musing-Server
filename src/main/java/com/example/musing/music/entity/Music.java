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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter // Lombok 어노테이션 : 클래스 내 모든 필드의 Getter 메소드 자동 생성
@NoArgsConstructor // Lombok 어노테이션 : 기본 생성자 자동 추가
@Entity
@Table(name="music")
public class Music {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="musicId")
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String genre;

    @Column
    private String playtime;


    private String albumName;

    @Column
    private String mood;


    @Column(nullable = false)
    private String songLink;

    @Column
    private String thumbNailLink;

    // 음악과 게시판 일대다 관계 매핑
    @OneToMany(mappedBy = "music" , cascade = CascadeType.ALL, orphanRemoval = true )
    private List<Board> boardList = new ArrayList<Board>();

    // 음악과 해쉬태그 일대다 관계 매핑
    @OneToMany(mappedBy = "music" , cascade = CascadeType.ALL, orphanRemoval = true )
    private List<HashTag> hashTagList = new ArrayList<HashTag>();
    // 음악과 좋아요 일대다 관계 매핑
    @OneToMany(mappedBy = "music", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<Like_Music> preferMusics = new ArrayList<Like_Music>();

    //아티스트와 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "artist", nullable = false)
    private Artist artist;


    @Builder
    public Music(String name,Artist artist,String genre,String mood,String playtime,String albumName,String songLink,String thumbNailLink) {
        this.name = name;
        this.artist = artist;
        this.genre = genre;
        this.playtime = playtime;
        this.albumName = albumName;
        this.songLink = songLink;
        this.mood = mood;
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

    public List<String> getGenreList() {
        return convertStringToList(genre);
    }

    public List<String> getMoodList() {
        return convertStringToList(mood);
    }

    private List<String> convertStringToList(String str) {
        if (str == null || str.isEmpty()) {
            return List.of(); // 빈 문자열일 경우 빈 리스트 반환
        }
        // 대괄호 제거 및 공백 제거 후, 쉼표로 분리하여 리스트로 변환
        return Arrays.stream(str.replaceAll("[\\[\\]\\s]", "").split(","))
                .collect(Collectors.toList());
    }

   //엘범 null 삽입 방지
    @PrePersist
    private void prePersist() {


        if (this.albumName == null) {
            this.albumName = "Unknown";
        }


        if(this.mood == null) {
            this.mood = "N/A";
        }

        if(this.playtime == null) {
            this.playtime = "N/A";
        }

        if(this.thumbNailLink == null) {
            this.thumbNailLink = "NoImage";
        }
    }



}
