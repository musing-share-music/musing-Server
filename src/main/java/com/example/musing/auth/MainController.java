package com.example.musing.auth;

import com.example.musing.auth.JWT.DTO.PrincipalDetails;
import com.example.musing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/musing")
@RequiredArgsConstructor
public class MainController {
    //http://localhost:8090/oauth2/authorization/google //구글 로그인
    private final UserRepository userRepository;//서비스단으로 변경예정
    @GetMapping("main22")
    public ResponseEntity<String> loginCheck(Principal principal){
        return ResponseEntity.ok("로그인 성공, 유저이름 : "+ userRepository.findById(principal.getName()).get().getUsername());
    }
}
