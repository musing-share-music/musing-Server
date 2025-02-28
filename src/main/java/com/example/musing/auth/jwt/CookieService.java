package com.example.musing.auth.jwt;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class CookieService {
    //쿠키 생성
    public Cookie generateCookie(String accessToken){
        String cookieName = "accessToken";
        String cookieValue = accessToken;
        Cookie cookie = new Cookie(cookieName,cookieValue);

        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60*30);

        return cookie;
    }
}
