package com.example.musing.auth.jwt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,String> {
    Optional<Token> findByAccesstoken(String accessToken);
    void deleteByAccesstoken(String accessToken);
}
