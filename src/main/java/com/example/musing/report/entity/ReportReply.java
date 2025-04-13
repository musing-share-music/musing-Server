package com.example.musing.report.entity;

import com.example.musing.reply.entity.Reply;
import com.example.musing.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
@Getter // Lombok 어노테이션 : 클래스 내 모든 필드의 Getter 메소드 자동 생성
@NoArgsConstructor // Lombok 어노테이션 : 기본 생성자 자동 추가
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name="reportreply")
public class ReportReply {
    //신고 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="reportid")
    private long id;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime reportDate;

    @Column(nullable = false,length = 500)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userid")
    private User user;

    //관계설정 답글에 관한 외래키 보유 주인테이블
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="replyid")
    private Reply reply;

    @Column
    @ColumnDefault("false")
    private boolean isDelete;

    @Builder
    public ReportReply(String content, Reply reply, User user){
        this.content = content;
        this.reply = reply;
        this.user = user;
    }

    public void delete() {
        this.isDelete = true;
    }
}
