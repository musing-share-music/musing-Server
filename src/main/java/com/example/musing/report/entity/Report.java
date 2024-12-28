package com.example.musing.report.entity;

import com.example.musing.board.entity.Board;
import com.example.musing.reply.entity.Reply;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
@Getter // Lombok 어노테이션 : 클래스 내 모든 필드의 Getter 메소드 자동 생성
@NoArgsConstructor // Lombok 어노테이션 : 기본 생성자 자동 추가
@Entity
@Table(name="report")
public class Report {

    //신고 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="reportid")
    private long id;

    @Column(nullable = false)
    private Date reportedat;

    @Column(nullable = false,length = 500)
    private String content;


    //관계설정 게시판에 관한 외래키 보유 주인테이블
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="boardid", nullable = false)
    private Board board;

    //관계설정 답글에 관한 외래키 보유 주인테이블
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="replyid", nullable = false)
    private Reply reply;
}
