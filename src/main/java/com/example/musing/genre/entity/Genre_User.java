package com.example.musing.genre.entity;

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
@Table(name = "genre_user")
@Entity
public class Genre_User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genreid", nullable = false)
    private Genre genre;

    public static Genre_User of(User user, Genre genre) {
        return Genre_User.builder()
                .user(user)
                .genre(genre)
                .build();
    }
}
