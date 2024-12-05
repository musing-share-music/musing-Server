package com.example.musing.auth.handler;

import com.example.musing.auth.JWT.TokenProvider;
import com.example.musing.auth.exception.AuthorityException;
import com.example.musing.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //엑세스 토큰 및 리프래시 토큰 생성
        String accessToken = tokenProvider.generateAccessToken(authentication);
        tokenProvider.generateRefreshToken(authentication,accessToken);

        // 쿠키를 통해 토큰을 클라이언트에 전달하는거 찾아봐야함
        //메인페이지 이동으로 넣고 따로 헤더랑 쿠키로 보낼예정
        //관리자일때랑 아닐때 구분해서 리다이렉트 하게하는게 좋을듯?
        // 실패시 쿠키 삭제도 넣기
        OAuth2User oAuth2User = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(oAuth2User.getAuthorities().contains("ROLE_ADMIN")){
            response.sendRedirect("/");
        }else if(oAuth2User.getAuthorities().contains("ROLE_USER")){
            response.sendRedirect("/");
        }else{
            throw new AuthorityException(ErrorCode.TOKEN_EXPIRED);
        }

    }
}
