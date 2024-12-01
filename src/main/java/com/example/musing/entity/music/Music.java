package com.example.musing.entity.music;

import com.example.musing.entity.Board.Board;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.awt.print.Book;
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

    @Column(nullable = false)
    private String artist;

    @Column(nullable = true)
    private String genre;

    @Column(nullable = false)
    private String playtime;

    @Column(nullable = true)
    private String album;

    // 음악과 게시판 일대다 관계 매핑
    @OneToMany(mappedBy = "music" , cascade = CascadeType.ALL, orphanRemoval = true )
    private List<Board> boardList = new ArrayList<Board>();
    // 음악과 선호도 일대다 관계 매핑
    @OneToMany(mappedBy = "music" , cascade = CascadeType.ALL, orphanRemoval = true )
    private List<Prefer> PreferList = new ArrayList<Prefer>();
    // 음악과 해쉬태그 일대다 관계 매핑
    @OneToMany(mappedBy = "music" , cascade = CascadeType.ALL, orphanRemoval = true )
    private List<HashTag> HashTagList = new ArrayList<HashTag>();

}
