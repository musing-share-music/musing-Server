package com.example.musing.notice.entity;

import com.example.musing.common.jpa.BaseEntity;
import com.example.musing.notice.dto.NoticeRequestDto;
import com.example.musing.reply.entity.Reply;
import com.example.musing.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter // Lombok 어노테이션 : 클래스 내 모든 필드의 Getter 메소드 자동 생성
@NoArgsConstructor // Lombok 어노테이션 : 기본 생성자 자동 추가
@Entity
@Table(name = "notice")
public class Notice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noticeid")
    private long id;
    //글 제목
    @Column(nullable = false, length = 100)
    private String title;
    //글 내용
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column
    private List<String> images;

    @Column(nullable = false)
    private boolean activeCheck;

    //관계설정 유저에 관한 외래키 보유 주인테이블
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    @Builder
    public Notice(String title, String content, User user, List<String> images){
        this.title = title;
        this.content = content;
        this.user = user;
        this.images = images;
    }
}
