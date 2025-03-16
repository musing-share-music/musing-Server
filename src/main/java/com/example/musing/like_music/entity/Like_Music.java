package com.example.musing.like_music.entity;

import com.example.musing.music.entity.Music;
import com.example.musing.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "like_music", uniqueConstraints =
@UniqueConstraint(columnNames = {"user_id", "music_id"}))
public class Like_Music {

    //유저가 좋아요를 눌렀을때에 관한 객체
    //유저는 여러개의 노래에 좋아요를 누를 수 있고 하나의 노래엔 여러 유저로부터 좋아요를 받을 수 있기 위한
    // 일대다 중간 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="like_musicid")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musicid")
    private Music music;

    @Builder
    public Like_Music(User user, Music music) {
        this.user = user;
        this.music = music;
    }

    public static Like_Music of(User user, Music music) {
        return Like_Music.builder()
                .user(user)
                .music(music)
                .build();
    }
}
