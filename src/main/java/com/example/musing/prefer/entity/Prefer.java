package com.example.musing.prefer.entity;

import com.example.musing.music.entity.Music;
import com.example.musing.like_music.entity.Like_Music;
import com.example.musing.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter // Lombok 어노테이션 : 클래스 내 모든 필드의 Getter 메소드 자동 생성
@NoArgsConstructor // Lombok 어노테이션 : 기본 생성자 자동 추가
@Entity
@Table(name="prefer")
public class Prefer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String genre;
    @Column
    private String nation;
    @Column
    private String mood;
    //유저에 관한 외래키 보유 주인테이블
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_id",nullable = false)
    private Music music_id;

    @OneToMany(mappedBy = "prefer_id", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<Like_Music> preferMusics = new ArrayList<Like_Music>();
}
