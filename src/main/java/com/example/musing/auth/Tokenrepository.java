package com.example.musing.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Tokenrepository extends JpaRepository<Token,String> {
    Optional<Token> findByAccessToken(String accessToken);
}
