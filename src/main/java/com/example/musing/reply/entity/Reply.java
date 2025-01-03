package com.example.musing.reply.entity;

import com.example.musing.board.entity.Board;
import com.example.musing.common.jpa.BaseEntity;
import com.example.musing.report.entity.Report;
import com.example.musing.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter // Lombok 어노테이션 : 클래스 내 모든 필드의 Getter 메소드 자동 생성
@NoArgsConstructor // Lombok 어노테이션 : 기본 생성자 자동 추가
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reply")
public class Reply extends BaseEntity {

    //댓글 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "replyid")
    private long id;

    @Column(nullable = false)
    private Long starScore;

    @Column(nullable = true)
    private String content;

    //관계설정 유저에 관한 외래키 보유 주인테이블
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    //관계설정 게시판에 관한 외래키 보유 주인테이블
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boardid", nullable = false)
    private Board board;

    //일대다 관계로 신고테이블과 메핑
    @OneToMany(mappedBy = "reply", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reportList = new ArrayList<Report>();

    public static Reply from(ReplyDto replyDto) {
        return Reply.builder()
            .user(user)
            .build();
    }
}
