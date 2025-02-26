package com.example.musing.auth.filter;

import com.example.musing.auth.jwt.TokenProvider;
import com.example.musing.auth.jwt.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final TokenService tokenService;

    //엑세스 토큰 및 리프래쉬 토큰 확인
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //이 필터는 애초에 토큰이 있다는 가정하에 아래단이 시작됨
        String accessToken = resolveToken(request);
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (tokenProvider.validateToken(accessToken)) { //유효기간이 남았다면 통과
            setAuthentication(accessToken);//시큐리티 콘텍스트 추가
        }

        //리프래시 토큰이 만료되었거나 값이 없으면 통과
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String accessToken) {
        //시큐리티 context에 등록할 Authentication 생성 및 등록
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> "accessToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
