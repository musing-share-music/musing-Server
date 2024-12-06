package com.example.musing.auth.filter;

import com.example.musing.auth.JWT.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    //엑세스 토큰 및 리프래쉬 토큰 확인
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String accessToken = resolveToken(request);

        //엑세스 토큰 검증
        if (tokenProvider.validateToken(accessToken)) { //유효기간이 남았다면 통과
            setAuthentication(accessToken);//시큐리티 콘텍스트 추가
        } else {
            // 만료되었을 경우 accessToken 재발급
            String reissueAccessToken = tokenProvider.reissueAccessToken(accessToken);
            if (StringUtils.hasText(reissueAccessToken)) {//재발급이 성공했다면
                setAuthentication(reissueAccessToken);//시큐리티 콘텍스트 추가

                // 재발급된 accessToken 다시 전달
                response.setHeader(AUTHORIZATION, "Bearer " + reissueAccessToken);
            }else{//리프래시 토큰이 만료되어 재발급 실패, null값을 받음
                //tokenProvider.validateToken를 사용해서 리프래시토큰의 유효기간을 확인할 수 있지만,
                //tokenProvider.reissueAccessToken안에 이미 포함된 로직이라 생략
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String newAccessToken = tokenProvider.generateAccessToken(authentication);//엑세스 토큰 생성
                tokenProvider.generateRefreshToken(authentication,newAccessToken);
                //실패했으니 예외처리 이후 리프래시 토큰 재발급 받고 다시 로그인 시켜야함
            }
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String accessToken) {
        //시큐리티 context에 등록할 Authentication 생성 및 등록
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION);
        if (ObjectUtils.isEmpty(token) || !token.startsWith("Bearer ")) {
            return null;
        }
        return token.substring("Bearer ".length());
    }
}
