package com.example.musing.user.entity;

import com.example.musing.board.entity.Board;
import com.example.musing.reply.entity.Reply;
import com.example.musing.playlist.entity.PlayList;
import com.example.musing.prefer.entity.Prefer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter // Lombok 어노테이션 : 클래스 내 모든 필드의 Getter 메소드 자동 생성
@NoArgsConstructor // Lombok 어노테이션 : 기본 생성자 자동 추가
@Entity
@Table(name = "user")
public class User {


    @Id
    @GeneratedValue
    @Column(name="userid")// AUTO_INCREMENT
    private Long id;

    @Column(nullable = false, length = 50)
    private String username; // 사용자 이름

    @Column(nullable = false, unique = true, length = 100)
    private String email; // 이메일 (유니크)

    @Column(nullable = false)
    private String password;

    // true면 사용가능 false면 정지상태
    @Column(nullable = false)
    private boolean Activated;




    //게시판 일대다 매핑
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boards = new ArrayList<>();
    //플레이 리스트 일대다 매핑
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayList> playLists = new ArrayList<>();
    //플레이 리스트 일대다 매핑
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prefer> prefers = new ArrayList<>();
    //답글 리스트와 일대다 매핑
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replyList = new ArrayList<>();


}
