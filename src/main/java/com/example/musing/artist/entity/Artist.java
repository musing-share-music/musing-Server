package com.example.musing.artist.entity;

import com.example.musing.music.entity.Music;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Getter // Lombok 어노테이션 : 클래스 내 모든 필드의 Getter 메소드 자동 생성
@NoArgsConstructor // Lombok 어노테이션 : 기본 생성자 자동 추가
@Entity
@Table(name="artist")
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="artistid")
    private long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "artist" , cascade = CascadeType.ALL, orphanRemoval = true )
    private List<Artist_Music> artistMusics = new ArrayList<>();


    @Builder
    public Artist(String name) {
        this.name = name;

    }

}
