package com.example.musing.user.entity;

import com.example.musing.board.entity.Board;
import com.example.musing.like_music.entity.Like_Music;
import com.example.musing.playlist.entity.PlayList;
import com.example.musing.reply.entity.Reply;
import com.example.musing.report.entity.ReportBoard;
import com.example.musing.report.entity.ReportReply;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Getter // Lombok 어노테이션 : 클래스 내 모든 필드의 Getter 메소드 자동 생성
@NoArgsConstructor // Lombok 어노테이션 : 기본 생성자 자동 추가
@Entity
@Table(name = "user")
@DynamicInsert
@DynamicUpdate  //update 될때 바뀐 컬럼만 변경하도록 함, 기존에는 모든 컬럼을 가져와서 바꿈
public class User { //https://developers.google.com/identity/openid-connect/openid-connect?hl=ko#an-id-tokens-payload

    @Id
    @Column(name = "userid")// AUTO_INCREMENT 제거(구글 전용 아이디 키)
    private String id;

    @Column(nullable = false, length = 50)
    private String username; // 사용자 이름

    @Column(nullable = false, unique = true, length = 100)
    private String email; // 이메일 (유니크)

    @Column(nullable = false)
    private String profile; //구글 프로필 url저장소

    @Column
    private String youtubeId; //유튜브 id

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role; //사용자, 관리자 구분

    // true면 사용가능 false면 모달 선택을 안한 상태
    @Column
    private boolean activated;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Status status;

    //게시판 일대다 매핑
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boards = new ArrayList<>();
    //플레이 리스트 일대다 매핑
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayList> playLists = new ArrayList<>();

    //유저가 선호하는 장르 및 분위기
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User_LikeMood> moods = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User_LikeGenre> genres = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User_LikeArtist> artists = new ArrayList<>();

    //답글 리스트와 일대다 매핑
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replyList = new ArrayList<>();
    //유저가 음악에 좋아요를 눌렀을때의 관계
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like_Music> likes = new ArrayList<>();

    //유저의 신고관련 일대다 매핑
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportBoard> reportBoards = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportReply> reportReplies = new ArrayList<>();

    @Builder
    public User(String id, String username, String email, String profile, String youtubeId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profile = profile;
        this.youtubeId = youtubeId;
        this.role = Role.USER;
        this.status = Status.ACTIVE;
    }

    public void updateGoogleInfo(String username, String profile, String youtubeId) {
        this.username = username;
        this.profile = profile;
        this.youtubeId = youtubeId;
    }

    public void updateactivated(boolean activated) {
        this.activated = activated;
    }

    public void withDraw(){
        String text = "탈퇴한 사용자";

        this.status = Status.WITHDRAW;
        // 유저 정보 변경하기
        this.username = text;
        this.email = text +" - "+ id; //개인정보 파기를 하지만 유니크 설정이기에 id를 붙이기로 함
        this.profile = text;
        this.youtubeId = null;
    }
}
