package com.example.musing.auth.filter;

import com.example.musing.auth.exception.TokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class TokenExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //에러코드 확인
        try{
            filterChain.doFilter(request,response);
        } catch(TokenException e){
            response.sendError(e.getErrorCode().getHttpStatus().value(),e.getMessage());
        }
    }
}
