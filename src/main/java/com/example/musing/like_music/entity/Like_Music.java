package com.example.musing.like_music.entity;

import com.example.musing.music.entity.Music;
import com.example.musing.prefer.entity.Prefer;
import com.example.musing.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Like_Music {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long prefer_music_id;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_id")
    private Music music_id;
}
