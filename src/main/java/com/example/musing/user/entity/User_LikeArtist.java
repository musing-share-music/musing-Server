package com.example.musing.user.entity;

import com.example.musing.artist.entity.Artist;
import com.example.musing.genre.entity.Genre;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_likeartist")
@Entity
public class User_LikeArtist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artistid", nullable = false)
    private Artist artist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    public static User_LikeArtist of(Artist artist, User user) {
        return User_LikeArtist.builder()
                .artist(artist)
                .user(user)
                .build();
    }
}
