package com.example.musing.mood.entity;

import com.example.musing.genre.entity.Genre;
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
@Table(name = "mood_user")
@Entity
public class Mood_User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moodid", nullable = false)
    private Mood mood;
    
    public static Mood_User of(User user, Mood mood) {
        return Mood_User.builder()
                .user(user)
                .mood(mood)
                .build();
    }
}
