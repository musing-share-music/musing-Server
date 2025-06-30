package com.example.musing.auth.controller;

import com.example.musing.auth.jwt.TokenService;
import com.example.musing.common.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "토큰 재발급 API")
@RestController
@RequestMapping("/musing")
@RequiredArgsConstructor
public class AuthController {
    private final TokenService tokenService;

    @Operation(summary = "토큰 재발급", description = "401에러일 경우에만 해당 api를 호출하며," +
            " 파라미터로 유저 email을 전달해서 실행합니다.")
    @GetMapping("/auth/reissue")
    public ResponseDto<String> reissueToken(@RequestParam String userId,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {

        tokenService.reissueToken(userId, request, response);
        return ResponseDto.of("", "토큰 재발급에 성공했습니다.");
    }
}
