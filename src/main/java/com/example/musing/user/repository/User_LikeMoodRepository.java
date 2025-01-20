package com.example.musing.user.repository;

import com.example.musing.user.entity.User;
import com.example.musing.user.entity.User_LikeMood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface User_LikeMoodRepository extends JpaRepository<User_LikeMood, Long> {
    boolean existsByUser(User user);
}
