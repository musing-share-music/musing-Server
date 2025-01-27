package com.example.musing.user.entity;

import com.example.musing.mood.entity.Mood;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_likemood")
@Entity
public class User_LikeMood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moodid", nullable = false)
    private Mood mood;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    public static User_LikeMood of(Mood mood, User user) {
        return User_LikeMood.builder()
                .mood(mood)
                .user(user)
                .build();
    }
}
