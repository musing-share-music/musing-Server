package com.example.musing.user.repository;

import com.example.musing.user.entity.User;
import com.example.musing.user.entity.User_LikeGenre;
import com.example.musing.user.entity.User_LikeMood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface User_LikeMoodRepository extends JpaRepository<User_LikeMood, Long> {
    boolean existsByUser(User user);

    List<User_LikeMood> findByUser(User user);
}
