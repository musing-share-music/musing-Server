package com.example.musing.auth.handler;

import com.example.musing.auth.jwt.CookieService;
import com.example.musing.auth.jwt.Token;
import com.example.musing.auth.jwt.TokenService;
import com.example.musing.auth.exception.AuthorityException;
import com.example.musing.auth.oauth2.service.Oauth2ProviderTokenService;
import com.example.musing.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {
    private final CookieService cookieService;
    private final TokenService tokenService;
    private final OAuth2AuthorizedClientManager OAuth2AuthorizedClientManager;
    private final Oauth2ProviderTokenService oauth2ProviderTokenService;
    @Value("${client.host}")
    private String clientHost;
    private static final String ADMIN_URL = "/admin/notice";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String googleId = authentication.getName();

        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
        String clientRegistrationId = oauth2Token.getAuthorizedClientRegistrationId();

        OAuth2AuthorizedClient authorizedClient = OAuth2AuthorizedClientManager.authorize(
                OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)
                        .principal(authentication)
                        .build()
        );

        if (authorizedClient != null && authorizedClient.getRefreshToken() != null) {
            // 구글은 최초 로그인에만 가져와짐
            String refreshToken = authorizedClient.getRefreshToken().getTokenValue();
            log.info(refreshToken + "구글 리프래시 토큰");
            oauth2ProviderTokenService.renewOauth2ProviderToken(refreshToken, googleId);
        }

        String authorization = null;

        for (Cookie cookie : request.getCookies()) {//쿠키 여부 체크
            if (cookie.getName().equals("accessToken")) {
                authorization = cookie.getValue();
                log.info("쿠키 값 불러오기 완료");
                break;
            }
        }

        if (authorization == null) {
            //엑세스 토큰 및 리프래시 토큰 생성
            String accessToken = tokenService.generateAccessToken(authentication);
            cookieService.generateCookie(accessToken, response);

            String userId = authentication.getName();
            Optional<Token> token = tokenService.findById(userId);//리프래시 토큰있나 확인

            if (token.isPresent()) {//자신의 아이디의 리프래시토큰이 있나 검사, 임의로 쿠키또는 헤더값을 지웠을 경우로 작성
                //리프래쉬 토큰 로테이션을 위해 그냥 지우고 다시 생성
                tokenService.deleteRefreshToken(accessToken);
            }
            String ipAdress = tokenService.getClientIpAddress(request);
            String userAgent = tokenService.getClientUserAgent(request);
            tokenService.generateRefreshToken(null, authentication, ipAdress, userAgent);
        }
        //관리자일때랑 아닐때 구분해서
        OAuth2User oAuth2User = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String role = oAuth2User.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        switch (role) {
            case "ROLE_ADMIN":
                response.sendRedirect(clientHost+ADMIN_URL);
                break;
            case "ROLE_USER":
                response.sendRedirect(clientHost);
                break;
            default:
                throw new AuthorityException(ErrorCode.INVALID_AUTHORITY);
        }
    }
}
