package com.example.musing.auth.JWT;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,String> {
    Optional<Token> findByAccesstoken(String accessToken);
}
