package com.example.musing.user.repository;

import com.example.musing.user.entity.User;
import com.example.musing.user.entity.User_LikeGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface User_LikeGenreRepository extends JpaRepository<User_LikeGenre, Long> {
    boolean existsByUser(User user);
}
