package com.example.musing.auth.jwt;

import com.example.musing.auth.exception.TokenException;
import com.example.musing.exception.CustomException;
import com.example.musing.user.entity.User;
import com.example.musing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

import static com.example.musing.exception.ErrorCode.NOT_FOUND_USER;
import static com.example.musing.exception.ErrorCode.TOKEN_EXPIRED;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenProvider tokenProvider;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public void reissueToken(String email) {
        String memberId = getUser(email).getId();
        // 만료되었을 경우 accessToken 재발급
        //reissueAccessToken에 리프래시토큰 만료상태 확인 로직있음
        String reissueAccessToken = tokenProvider.reissueAccessToken(memberId);
        if (StringUtils.hasText(reissueAccessToken)) {//재발급이 성공했다면
            setAuthentication(reissueAccessToken);//시큐리티 콘텍스트 추가

            //새로 발급한 엑세스 토큰 쿠키로 반환
            tokenProvider.generateCookie(reissueAccessToken);

        } else {
            //리프래시토큰 만료되었을 경우
            deleteRefreshToken(memberId);

            throw new TokenException(TOKEN_EXPIRED);
        }
    }

    @Transactional
    public void saveOrUpdate(Token token, String memberKey, String accessToken, String refreshToken) {
        if (token != null) {
            token.updateAccessToken(accessToken);
            token.updateRefreshToken(refreshToken); //새로만들어서 저장하지만 생성기간과 유효기간만 바뀜
        } else {//해당 유저 아이디의 리프래시 토큰이 없으면 새로생성
            token = new Token(memberKey, accessToken, refreshToken);
        }
        tokenRepository.save(token);
    }

    public Token findAccessToken(String memberId) {
        return tokenRepository.findById(memberId).orElseThrow(() -> new TokenException(TOKEN_EXPIRED));
    }

    @Transactional
    public void updateToken(String accessToken, Token token) {
        token.updateAccessToken(accessToken);
        tokenRepository.save(token);
    }

    public Optional<Token> findById(String TokenId) {
        return tokenRepository.findById(TokenId);
    }

    @Transactional
    public void deleteRefreshToken(String memberId) {
        tokenRepository.deleteById(memberId);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

    }

    private void setAuthentication(String accessToken) {
        //시큐리티 context에 등록할 Authentication 생성 및 등록
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
