package com.example.musing.board.entity;

import com.example.musing.board.dto.PostDto;
import com.example.musing.common.jpa.BaseEntity;
import com.example.musing.exception.CustomException;
import com.example.musing.music.entity.Music;
import com.example.musing.reply.entity.Reply;
import com.example.musing.report.entity.ReportBoard;
import com.example.musing.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.musing.board.entity.CheckRegister.NON_CHECK;
import static com.example.musing.exception.ErrorCode.NOT_FOUND_USER;

@Getter // Lombok 어노테이션 : 클래스 내 모든 필드의 Getter 메소드 자동 생성
@NoArgsConstructor// Lombok 어노테이션 : 기본 생성자 자동 추가
@Entity
@Table(name = "board")
public class Board extends BaseEntity {
    //글번호(자동증가)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "boardid")
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

    @ColumnDefault("0")
    @Column(nullable = false)
    private float rating;

    @ColumnDefault("0")
    @Column(nullable = false)
    private int replyCount;

    @Column(nullable = false)
    private boolean activeCheck;

    @ColumnDefault("NON_CHECK")
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private CheckRegister permitRegister;

    @Column
    private String images;


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
    private List<ReportBoard> Reports = new ArrayList<>();

    public List<String> getImageList() {
        if (images == null || images.isEmpty()) {
            return null;
        }

        String[] imageArray = images.substring(1, images.length() - 1).split(", ");
        return new ArrayList<>(Arrays.asList(imageArray));
    }

    @Builder
    public Board(String title, String content, boolean activeCheck, String image, int recommendCount, int viewCount, CheckRegister permitRegister ,User user,Music music) {
        this.title = title;
        this.content = content;
        this.recommendCount = 0; // 기본값 설정
        this.viewCount = 0; // 기본값 설정
        this.activeCheck = activeCheck;
        this.permitRegister = NON_CHECK;
        this.images = image;
        this.user = user;
        this.music = music;
    }

    public static Board of(User user, Music music, String title, String content, String images) {
        return Board.builder()
                .user(user)
                .music(music)
                .title(title)
                .content(content)
                .image(images)
                .activeCheck(true)
                .recommendCount(0)
                .viewCount(0)
                .build();
    }

    public Board updateBoard(Music music, String title, String content, String image) {
        this.music = music;
        this.title = title;
        this.content = content;
        this.images = image;
        return this;
    }
    public void updateRegister(CheckRegister checkRegister) {
        this.permitRegister = checkRegister;
    }

    public static PostDto toDto(Board board) {
        return PostDto.fromEntity(board);
    }

    public void delete() {
        this.activeCheck = false;
    }
}




