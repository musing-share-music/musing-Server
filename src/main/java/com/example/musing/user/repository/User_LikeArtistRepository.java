package com.example.musing.user.repository;

import com.example.musing.user.entity.User;
import com.example.musing.user.entity.User_LikeArtist;
import com.example.musing.user.entity.User_LikeMood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface User_LikeArtistRepository extends JpaRepository<User_LikeArtist, Long> {
    boolean existsByUser(User user);

    List<User_LikeArtist> findByUser(User user);
}
