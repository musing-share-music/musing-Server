package com.example.musing.hashtag.entity;

import com.example.musing.music.entity.Music;
import com.example.musing.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter // Lombok 어노테이션 : 클래스 내 모든 필드의 Getter 메소드 자동 생성
@NoArgsConstructor // Lombok 어노테이션 : 기본 생성자 자동 추가
@Entity
@Table(name="hashtag")
public class HashTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="hashtagid")
    private Long id;

    @Column(nullable = false)
    private String hashtag;

    //해쉬태그에 관한 외래키 보유 주인테이블
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musicid", nullable = false)
    private Music music; // 작성자

    @Builder
    public HashTag(String hashtag, Music music) {
        this.hashtag = hashtag;
        this.music = music;
    }


    public void setMusic(Music music) {
        this.music = music;
    }
}
