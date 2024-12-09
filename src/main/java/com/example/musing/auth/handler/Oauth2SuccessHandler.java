package com.example.musing.auth.handler;

import com.example.musing.auth.JWT.Token;
import com.example.musing.auth.JWT.TokenProvider;
import com.example.musing.auth.JWT.TokenRepository;
import com.example.musing.auth.JWT.TokenService;
import com.example.musing.auth.exception.AuthorityException;
import com.example.musing.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@Component
@Slf4j
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private final TokenService tokenService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String accessToken = tokenProvider.generateAccessToken(authentication);
        tokenProvider.generateRefreshToken(authentication, accessToken);
        /*        // 디버깅용: 요청의 모든 헤더 출력
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            log.info("Header: {} = {}", headerName, request.getHeader(headerName));
        }
        String authorization = request.getHeader(AUTHORIZATION);

        log.info(authorization);

        if(authorization == null){//엑세스 토큰이 없는 상황일때, 해당 부분 쿠키로 나중에 변경예정
            //엑세스 토큰 및 리프래시 토큰 생성
            String accessToken = tokenProvider.generateAccessToken(x    );
            response.setHeader(AUTHORIZATION, "Bearer " + accessToken);//헤더에 엑세스 토큰 추가
            Optional<Token> token = tokenService.findById(accessToken);
            if(token.isPresent()){//자신의 아이디의 리프래시토큰이 있나 검사
                //자신의 id값의 객체의 유효기간 체크를 해야함
                if(tokenProvider.validateToken(token.get().getRefreshtoken())){//유효기간이 남으면, 새로 생성한 엑세스 토큰만 갱신
                    tokenService.updateToken(accessToken,token.get());
                }//유효기간 지났으면 아래코드로 검증까지 포함
            }else {//아예 없으면 새로만들기
                tokenProvider.generateRefreshToken(authentication, accessToken);
            }
        }*/
        //이후 시큐리티 필터로 엑세스 토큰의 유효성을 검사함

        //관리자일때랑 아닐때 구분해서 리다이렉트 하게하는게 좋을듯?
        OAuth2User oAuth2User = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        for (GrantedAuthority authority : oAuth2User.getAuthorities()) {
            System.out.println("Authority: " + authority.getAuthority()); // 권한을 출력
        }
        if(oAuth2User.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            System.out.println("관리자");
/*            response.sendRedirect("/admin");*/
        }else if(oAuth2User.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"))) {
/*            response.sendRedirect("/main");*/
            System.out.println("사용자");
        }else{
            throw new AuthorityException(ErrorCode.INVALID_AUTHORITY);
        }

    }
}
