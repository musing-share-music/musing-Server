package com.example.musing.auth.oauth2.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Oauth2ProviderTokenInfo {
    @Value("${spring.security.oauth2.client.provider.google.token-uri}")
    private String googleTokenUri;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    public String getTokenUri() {
        return googleTokenUri;
    }

    public String getRequestBody(String refreshToken) {
        return "grant_type=refresh_token&refresh_token=" + refreshToken + "&client_id=" + googleClientId
                + "&client_secret=" + googleClientSecret;
    }
}
