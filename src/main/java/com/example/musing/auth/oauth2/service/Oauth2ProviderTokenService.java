package com.example.musing.auth.oauth2.service;

import com.example.musing.auth.oauth2.component.Oauth2ProviderTokenInfo;
import com.example.musing.auth.oauth2.entity.Oauth2ProviderToken;
import com.example.musing.auth.oauth2.repository.Oauth2ProviderTokenRepository;
import com.example.musing.exception.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static com.example.musing.exception.ErrorCode.ERROR;
import static com.example.musing.exception.ErrorCode.UNAUTHORIZED_OAUTH2_PROVIDER_TOKEN;

@RequiredArgsConstructor
@Service
public class Oauth2ProviderTokenService {
    private final Oauth2ProviderTokenRepository oauth2ProviderTokenRepository;
    private final Oauth2ProviderTokenInfo oauth2ProviderTokenInfo;

    // 최초 로그인 시 구글의 Oauth2 리프레쉬 토큰(자체 토큰아님)을 저장하기 위한 로직
    @Transactional
    public void renewOauth2ProviderToken(String refreshToken, String googleId) {
        Optional<Oauth2ProviderToken> token = oauth2ProviderTokenRepository.findByGoogleId(googleId);

        token.ifPresentOrElse(
                existingToken -> updateTokenIfNeeded(existingToken, refreshToken),
                () -> saveOauth2ProviderToken(refreshToken, googleId)
        );
    }

    @Transactional(readOnly = true)
    public String getGoogleProviderAccessToken(String userId) throws IOException, InterruptedException {
        Optional<Oauth2ProviderToken> oauth2ProviderToken =
                oauth2ProviderTokenRepository.findByGoogleId(userId);

        if (oauth2ProviderToken.isEmpty()) {
            throw new CustomException(ERROR);
        }

        return oauth2GetAccessToken(oauth2ProviderToken.get().getProviderRefreshToken());
    }

    private String oauth2GetAccessToken(String refreshToken)
            throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(oauth2ProviderTokenInfo.getTokenUri()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(oauth2ProviderTokenInfo.getRequestBody(refreshToken)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return getAccessToken(response);
    }

    private String getAccessToken(HttpResponse<String> response) throws JsonProcessingException {
        checkHttpStatusOk(response);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.body());

        return jsonNode.get("access_token").asText();
    }

    private void checkHttpStatusOk(HttpResponse<String> response) {
        if (response.statusCode() != HttpStatus.SC_OK) {
            throw new CustomException(UNAUTHORIZED_OAUTH2_PROVIDER_TOKEN);
        }
    }

    private void updateTokenIfNeeded(Oauth2ProviderToken existingToken, String refreshToken) {
        if (!existingToken.getProviderRefreshToken().equals(refreshToken)) {
            updateOauth2ProviderToken(existingToken, refreshToken);
        }
    }

    private void saveOauth2ProviderToken(String refreshToken, String googleId) {
        Oauth2ProviderToken token = Oauth2ProviderToken.of(refreshToken, googleId);
        oauth2ProviderTokenRepository.save(token);
    }

    private void updateOauth2ProviderToken(Oauth2ProviderToken token, String refreshToken) {
        token.updateRefreshToken(refreshToken);
    }

}
