package com.example.musing.prefer_music.playlist.entity;

import com.example.musing.music.entity.Music;
import com.example.musing.prefer.entity.Prefer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Prefer_Music {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long prefer_music_id;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prefer_id")
    private Prefer prefername;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_id")
    private Music musicname;
}
