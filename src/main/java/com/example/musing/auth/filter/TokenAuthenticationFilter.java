package com.example.musing.auth.filter;

import com.example.musing.auth.JWT.TokenProvider;
import com.example.musing.auth.JWT.TokenRepository;
import com.example.musing.auth.JWT.TokenService;
import com.example.musing.auth.exception.TokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
import java.util.Arrays;
import java.util.Objects;

import static com.example.musing.exception.ErrorCode.TOKEN_EXPIRED;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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
        }else {
            // 만료되었을 경우 accessToken 재발급
            //reissueAccessToken에 리프래시토큰 만료상태 확인 로직있음
            String reissueAccessToken = tokenProvider.reissueAccessToken(accessToken);
            logger.info("토큰값" + reissueAccessToken);
            if (StringUtils.hasText(reissueAccessToken)) {//재발급이 성공했다면
                setAuthentication(reissueAccessToken);//시큐리티 콘텍스트 추가

                //기존 쿠키 남은거 있나확인하고 없애고 새로 넣어주기
                //Null이 아니면 쿠키 삭제하기
                Objects.requireNonNull(checkCookie(request)).setMaxAge(0);
                //새로 발급한 엑세스 토큰 쿠키로 반환
                tokenProvider.generateCookie(reissueAccessToken);

            }else{
                //리프래시토큰 만료되었을 경우
                tokenService.deleteRefreshToken(accessToken);
                //쿠키 삭제
                Objects.requireNonNull(checkCookie(request)).setMaxAge(0);

                throw new TokenException(TOKEN_EXPIRED);//만료되었다는 예외처리
                //리프래시 토큰을 삭제해야 필터에 걸리지않고 로그인 다시하면 발급이 가능해짐
            }

        }
        //리프래시 토큰이 만료되었거나 값이 없으면 통과
        filterChain.doFilter(request, response);
    }
    private Cookie checkCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> "accessToken".equals(cookie.getName()))
                .findFirst()
                .orElse(null);
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
