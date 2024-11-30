package com.example.musing.Entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="Board")
public class Board {

    //글번호(자동증가)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    //글 제목
    @Column(nullable=false,length = 100)
    private String title;
    //글 내용
    @Column(nullable = false,columnDefinition = "TEXT")
    private String content;

    //생성일자
    @Column(nullable = false)
    private LocalDateTime createdAt;

    //관계설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userid", nullable = false)
    private User user;
}
