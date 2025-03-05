package com.example.musing.auth.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieService {
    //쿠키 생성
    public void generateCookie(String accessToken, HttpServletResponse response){
        String cookieName = "accessToken";
        String cookieValue = accessToken;
        Cookie cookie = new Cookie(cookieName,cookieValue);

        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60*30);

        response.addCookie(cookie);
    }
}
