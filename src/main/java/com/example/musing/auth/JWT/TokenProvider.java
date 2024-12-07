package com.example.musing.auth.JWT;

import com.example.musing.exception.ErrorCode;
import com.example.musing.auth.exception.TokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class TokenProvider {
    @Value("${key}")
    private String key;
    private SecretKey secretKey;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30L; //30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60L * 24 * 7; //7일
    private static final String KEY_ROLE = "role";
    private final TokenService tokenService;

    @PostConstruct
    private void setSecretKey() {
        secretKey = Keys.hmacShaKeyFor(key.getBytes());
    }
    //엑세스 토큰 생성
    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, ACCESS_TOKEN_EXPIRE_TIME);
    }

    //리프래쉬 토큰 생성 및 수정
    public void generateRefreshToken(Authentication authentication, String accessToken) {
        String refreshToken = generateToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);
        tokenService.saveOrUpdate(authentication.getName(),refreshToken,accessToken); //토큰 db에 저장하려는 부분 수정하기
    }
    private String generateToken(Authentication authentication, long expireTime) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expireTime);

        String authorities = authentication.getAuthorities().stream()//사용자의 권한 정보를 스트림으로 가져옴
                .map(GrantedAuthority::getAuthority)//권한 객체의 권한 이름을 가져옴
                .collect(Collectors.joining());

        return Jwts.builder()
                .setSubject(authentication.getName())//토큰 주체 설정 (유저고유 id)
                .claim(KEY_ROLE, authorities)//권한 정보를 토큰에 저장
                .setIssuedAt(now)//생성기간을 토큰에 저장
                .setExpiration(expiredDate)//유효기간을 토큰에 저장
                .signWith(secretKey, SignatureAlgorithm.HS256)//HMAC+SHA512 알고리즘과 비밀 키를 이용하여 토큰을 서명
                .compact();//문자열 변환
    }

    //엑세스 토큰 재발급
    public String reissueAccessToken(String accessToken){
        if(StringUtils.hasText(accessToken)){
            //엑세스 토큰값으로 리프래시토큰 조회
            Token token =  tokenService.issueAccessToken(accessToken);
            String refreshToken = token.getRefreshtoken();

            //리프래시 토큰 유효성 체크 후 엑세스 토큰 발급
            if (validateToken(refreshToken)) {
                String reissueAccessToken = generateAccessToken(getAuthentication(refreshToken));
                tokenService.updateToken(reissueAccessToken, token);
                return reissueAccessToken;
            }
        }
        return null;
    }

    //토큰 유효성 검사
    public boolean validateToken(String token){
        if(!StringUtils.hasText(token)){
            return false;
        }
        Claims claims = parseClaims(token);
        return claims.getExpiration().after(new Date()); //유효기간이 남으면 true
    }

    private Claims parseClaims(String token){
        try{//토큰의 payload부분 가져오기
            return Jwts.parserBuilder().setSigningKey(secretKey).build()
                    .parseClaimsJws(token).getBody();
        //필터단은 Advice사용 불가 servlet단에서만 가능하기에 별도의 예외처리
        }catch (ExpiredJwtException e){
            return e.getClaims();
        }catch (MalformedJwtException e){
            throw new TokenException(ErrorCode.INVALID_TOKEN);
        }catch (SecurityException e) {
            throw new TokenException(ErrorCode.INVALID_JWT_SIGNATURE);
        }
    }
    //권한 가져오기
    public List<SimpleGrantedAuthority> getAuthorities(Claims claims){
        return Collections.singletonList(new SimpleGrantedAuthority(
                claims.get(KEY_ROLE).toString()));
    }

    //유저 객체 생성 이후 시큐리티 ContextHolder의 Context에 등록
    public Authentication getAuthentication(String token){
        Claims claims = parseClaims(token);
        List<SimpleGrantedAuthority> authorityList = getAuthorities(claims);

        //시큐리티의 유저 클래스로 저장 (이름, 비밀번호는 oauth사용으로 없음, 권한)
        User user = new User(claims.getSubject(),"",authorityList);
        //유저 객체 생성 이후 인증 완료한 Authentication객체 생성
        return new UsernamePasswordAuthenticationToken(user, token, authorityList);
    }
}
