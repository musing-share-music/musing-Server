package com.example.musing.auth.controller;

import com.example.musing.auth.jwt.TokenService;
import com.example.musing.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/musing")
@RequiredArgsConstructor
public class AuthController {
    private final TokenService tokenService;

    @GetMapping("/auth/reissue")
    public ResponseDto<String> reissueToken(@RequestParam String email) {
        tokenService.reissueToken(email);
        return ResponseDto.of("", "토큰 재발급에 성공했습니다.");
    }
}
