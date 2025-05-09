package com.example.musing.auth.oauth2.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Table(name = "oauth2_provider_token")
@Entity
public class Oauth2ProviderToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "refresh_token")
    private String providerRefreshToken;

    private String googleId;

    @Builder
    private Oauth2ProviderToken(String providerRefreshToken, String googleId) {
        this.providerRefreshToken = providerRefreshToken;
        this.googleId = googleId;
    }

    public void updateRefreshToken(String providerRefreshToken){
        this.providerRefreshToken = providerRefreshToken;
    }

    public static Oauth2ProviderToken of(String providerRefreshToken, String googleId) {
        return Oauth2ProviderToken.builder()
                .providerRefreshToken(providerRefreshToken)
                .googleId(googleId).build();
    }
}