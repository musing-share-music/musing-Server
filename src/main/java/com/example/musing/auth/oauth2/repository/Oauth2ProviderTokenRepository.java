package com.example.musing.auth.oauth2.repository;

import com.example.musing.auth.oauth2.entity.Oauth2ProviderToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Oauth2ProviderTokenRepository extends JpaRepository<Oauth2ProviderToken, Long> {
    Optional<Oauth2ProviderToken> findByGoogleId(String googleId);
}
