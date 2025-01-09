package com.example.musing.board.entity;

import com.example.musing.common.jpa.BaseEntity;
import com.example.musing.music.entity.Music;
import com.example.musing.reply.entity.Reply;
import com.example.musing.report.entity.Report;
import com.example.musing.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter // Lombok 어노테이션 : 클래스 내 모든 필드의 Getter 메소드 자동 생성
@NoArgsConstructor// Lombok 어노테이션 : 기본 생성자 자동 추가
@Entity
@Table(name = "board")
public class Board extends BaseEntity {
    //글번호(자동증가)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="boardid")
    private long id;
    //글 제목
    @Column(nullable = false, length = 100)
    private String title;
    //글 내용
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    //추천수
    @ColumnDefault("0")
    @Column(nullable = false)
    private int recommendCount;

    @ColumnDefault("0")
    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private boolean activeCheck;

    @Column(nullable = false)
    private boolean permitRegister;

    @Column
    private String image;

    @Column(nullable = false)
    private String youtubeLink;

    //관계설정 유저에 관한 외래키 보유 주인테이블
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    //관계설정 노래에 관한 외래키 보유 주인테이블
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musicid", nullable = false)
    private Music music;


    //댓글과 일대다관계 매핑
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    //신고내역과 일대다관계 매핑
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> Reports = new ArrayList<>();

    @Builder
    public Board(String title, String content, User user, Music music, boolean activeCheck,String image,String youtubeLink) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.music = music;
        this.createdAt = LocalDateTime.now(); // 생성 시점 기록
        this.recommendCount = 0; // 기본값 설정
        this.viewCount = 0; // 기본값 설정
        this.activeCheck = activeCheck;
        this.image = image;
        this.youtubeLink = youtubeLink;
    }

}




