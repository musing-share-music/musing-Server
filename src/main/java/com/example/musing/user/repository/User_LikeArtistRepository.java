package com.example.musing.user.repository;

import com.example.musing.user.entity.User;
import com.example.musing.user.entity.User_LikeArtist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface User_LikeArtistRepository extends JpaRepository<User_LikeArtist, Long> {
    boolean existsByUser(User user);

}
