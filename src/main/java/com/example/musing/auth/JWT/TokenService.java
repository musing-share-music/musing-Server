package com.example.musing.auth.JWT;

import com.example.musing.auth.exception.TokenException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.musing.exception.ErrorCode.TOKEN_EXPIRED;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenRepository tokenRepository;

    @Transactional
    public void saveOrUpdate(Token token, String memberKey, String accessToken, String refreshToken) {
        if(token!=null){
            token.updateAccessToken(accessToken);
            token.updateRefreshToken(refreshToken); //새로만들어서 저장하지만 생성기간과 유효기간만 바뀜
        }else{//해당 유저 아이디의 리프래시 토큰이 없으면 새로생성
             token = new Token(memberKey, accessToken, refreshToken);
        }
        tokenRepository.save(token);
    }
    public Token issueAccessToken(String accessToken){
        return tokenRepository.findByAccesstoken(accessToken).orElseThrow(() -> new TokenException(TOKEN_EXPIRED));
    }
    @Transactional
    public void updateToken(String accessToken, Token token) {
        token.updateAccessToken(accessToken);
        tokenRepository.save(token);
    }
    public Optional<Token> findById(String TokenId){
        return tokenRepository.findById(TokenId);
    }

    public void deleteRefreshToken(String accessToken){
        tokenRepository.deleteByAccesstoken(accessToken);
    }
}
