package com.example.musing.hash_music.entity;

import com.example.musing.hashtag.entity.HashTag;
import com.example.musing.music.entity.Music;
import com.example.musing.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "hash_music")
public class Hash_Music {

    //음악과 해쉬태그의 중간테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long hash_music_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtagId")
    private HashTag hashTag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musicId")
    private Music music;
}
