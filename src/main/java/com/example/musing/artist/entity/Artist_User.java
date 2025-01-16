package com.example.musing.artist.entity;

import com.example.musing.genre.entity.Genre;
import com.example.musing.genre.entity.Genre_Music;
import com.example.musing.music.entity.Music;
import com.example.musing.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "artist_user")
@Entity
public class Artist_User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", nullable = false)
    private Artist artist;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    private User user;


    public static Artist_User of(Artist artist, User user) {
        return Artist_User.builder()
                .artist(artist)
                .user(user)
                .build();
    }
}
