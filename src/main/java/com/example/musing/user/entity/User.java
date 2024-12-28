package com.example.musing.user.entity;

import com.example.musing.board.entity.Board;
import com.example.musing.like_music.entity.Like_Music;
import com.example.musing.reply.entity.Reply;
import com.example.musing.playlist.entity.PlayList;
import com.example.musing.prefer.entity.Prefer;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
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
    @Column(name="userid")// AUTO_INCREMENT 제거(구글 전용 아이디 키)
    private String id;

    @Column(nullable = false, length = 50)
    private String username; // 사용자 이름

    @Column(nullable = false, unique = true, length = 100)
    private String email; // 이메일 (유니크)
    
    @Column(nullable = false)
    private String profile; //구글 프로필 url저장소 (로그인테스트로 넣은 컬럼,사용 여부 미확정), 사진만 url가져오는걸로 바꿔야겠다
    
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role; //사용자, 관리자 구분

    // true면 사용가능 false면 정지상태, null일 경우 장르 및 분위기 선택 안한 상태
    @Column(nullable = true)
    private Boolean activated; //null허용을 위해 웨퍼클래스 타입 적용

    @Column
    private String likegenre; //자신이 좋아하는 장르

    @Column
    private String likemood; //자신이 좋아하는 분위기

    @Column
    private String likeartists; //자신이 좋아하는 아티스트들

    //게시판 일대다 매핑
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boards = new ArrayList<>();
    //플레이 리스트 일대다 매핑
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayList> playLists = new ArrayList<>();
    //플레이 리스트 일대다 매핑
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prefer> prefers = new ArrayList<>();
    //답글 리스트와 일대다 매핑
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replyList = new ArrayList<>();
    //유저가 음악에 좋아요를 눌렀을때의 관계
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like_Music> likes = new ArrayList<>();

    @Builder
    public User(String id,String username, String email, String profile){
        this.id = id;
        this.username = username;
        this.email=email;
        this.profile=profile;
        this.role = Role.USER;
    }
    public void updateGoogleInfo(String username, String profile){
        this.username =username;
        this.profile = profile;
    }
    public void updateGenre(String genre){
        this.likegenre = genre;
    }
    public void updateMood(String mood){
        this.likemood = mood;
    }
    public void updateArtists(String artists){
        this.likeartists = artists;
    }

}
