package com.example.musing.reply.entity;

import com.example.musing.board.entity.Board;
import com.example.musing.common.jpa.BaseEntity;
import com.example.musing.reply.dto.ReplyDto;
import com.example.musing.report.entity.Report;
import com.example.musing.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Table(name = "reply")
@Entity
public class Reply extends BaseEntity {

    //댓글 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "replyid")
    private Long id;

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
    @Builder
    public Reply(long starScore, String content, User user, Board board) {
        this.starScore = starScore;
        this.content = content;
        this.user = user;
        this.board = board;
    }

    public void updateReply(long starScore, String content){
        this.starScore = starScore;
        this.content = content;
    }

    public static Reply from(ReplyDto replyDto, User user, Board board) {
        return Reply.builder()
                .starScore(replyDto.starScore())
                .content(replyDto.content())
                .user(user)
                .board(board)
                .build();
    }

}
