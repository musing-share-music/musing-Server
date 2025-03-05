package com.example.musing.auth.jwt;

import com.example.musing.auth.exception.TokenException;
import com.example.musing.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.musing.exception.ErrorCode.NEW_ENVIRONMENT_LOGIN;
import static com.example.musing.exception.ErrorCode.TOKEN_EXPIRED;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TokenService {
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30L; //30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60L * 24 * 7; //7일

    private static final String[] HEADERS = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"};

    private static final String KEY_ROLE = "role";
    private final TokenRepository tokenRepository;
    private final CookieService cookieService;
    @Value("${key}")
    private String key;
    private SecretKey secretKey;

    @PostConstruct
    private void setSecretKey() {
        secretKey = Keys.hmacShaKeyFor(key.getBytes());
    }


    //엑세스 토큰 생성
    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, ACCESS_TOKEN_EXPIRE_TIME);
    }

    //리프래쉬 토큰 생성 및 수정
    @Transactional
    public void generateRefreshToken(Token token, Authentication authentication, String ipAdress, String userAgent) {
        String refreshToken = generateToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);
        saveOrUpdate(token, authentication.getName(), ipAdress, userAgent, refreshToken); //토큰 db에 저장하려는 부분 수정하기
    }

    //토큰 유효성 검사
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        Claims claims = parseClaims(token);
        return claims.getExpiration().after(new Date()); //유효기간이 남으면 true
    }

    //권한 가져오기
    public List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections.singletonList(new SimpleGrantedAuthority(
                claims.get(KEY_ROLE).toString()));
    }

    //유저 객체 생성 이후 시큐리티 ContextHolder의 Context에 등록
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        List<SimpleGrantedAuthority> authorityList = getAuthorities(claims);

        //시큐리티의 유저 클래스로 저장 (이름, 비밀번호는 oauth사용으로 없음, 권한)
        User user = new User(claims.getSubject(), "", authorityList);
        //유저 객체 생성 이후 인증 완료한 Authentication객체 생성
        return new UsernamePasswordAuthenticationToken(user, token, authorityList);
    }

    @Transactional
    public void reissueToken(String userId, HttpServletRequest request, HttpServletResponse response) {
        String ipAddress = getClientIpAddress(request);
        String userAgent = getClientUserAgent(request);

        validateUserInfo(userId, ipAddress, userAgent);

        String reissueAccessToken = reissueAccessToken(userId);

        if (StringUtils.hasText(reissueAccessToken)) {//재발급이 성공했다면
            setAuthentication(reissueAccessToken);//시큐리티 콘텍스트 추가

            //새로 발급한 엑세스 토큰 쿠키로 반환
            cookieService.generateCookie(reissueAccessToken, response);


        } else {
            //리프래시토큰 만료되었을 경우
            deleteRefreshToken(userId);

            throw new TokenException(TOKEN_EXPIRED);
        }
    }

    public String getClientIpAddress(HttpServletRequest request) {

        for (String header : HEADERS) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0];
            }
        }

        return request.getRemoteAddr();
    }

    public String getClientUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    public Optional<Token> findById(String TokenId) {
        return tokenRepository.findById(TokenId);
    }

    @Transactional
    public void deleteRefreshToken(String userId) {
        tokenRepository.deleteById(userId);
    }

    //엑세스 토큰 재발급
    private String reissueAccessToken(String userId) {
        if (userId != null) {
            //엑세스 토큰값으로 리프래시토큰 조회
            Token token = findByUserId(userId);
            String refreshToken = token.getRefreshtoken();
            //리프래시 토큰 유효성 체크 후 엑세스 토큰 발급
            if (validateToken(refreshToken)) {
                return generateAccessToken(getAuthentication(refreshToken));
            }
        }
        return null;
    }

    private void validateUserInfo(String userId, String ipAddress, String userAgent) {
        Token token = findByUserId(userId);
        if (!(token.getIpAdress().equals(ipAddress) && token.getUserAgent().equals(userAgent))) {
            throw new TokenException(NEW_ENVIRONMENT_LOGIN);
        }
    }

    private void saveOrUpdate(Token token, String memberKey, String ipAdress, String userAgent, String refreshToken) {
        if (token != null) {
            token.updateRefreshToken(ipAdress, userAgent, refreshToken); //새로만들어서 저장하지만 생성기간과 유효기간만 바뀜
        } else {//해당 유저 아이디의 리프래시 토큰이 없으면 새로생성
            token = new Token(memberKey, ipAdress, userAgent, refreshToken);
        }
        tokenRepository.save(token);
    }

    private Token findByUserId(String userId) {
        return tokenRepository.findById(userId).orElseThrow(() -> new TokenException(TOKEN_EXPIRED));
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

    private Claims parseClaims(String token) {
        try {//토큰의 payload부분 가져오기
            return Jwts.parserBuilder().setSigningKey(secretKey).build()
                    .parseClaimsJws(token).getBody();
            //필터단은 Advice사용 불가 servlet단에서만 가능하기에 별도의 예외처리
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (MalformedJwtException e) {
            throw new TokenException(ErrorCode.INVALID_TOKEN);
        } catch (SecurityException e) {
            throw new TokenException(ErrorCode.INVALID_JWT_SIGNATURE);
        }
    }

    private void setAuthentication(String accessToken) {
        //시큐리티 context에 등록할 Authentication 생성 및 등록
        Authentication authentication = getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
