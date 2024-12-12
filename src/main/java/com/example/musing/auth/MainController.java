package com.example.musing.auth;

import com.example.musing.user.repository.UserRepository;
import com.example.musing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Objects;

@RestController
@RequestMapping("/musing")
@RequiredArgsConstructor
public class MainController {
    //http://localhost:8090/oauth2/authorization/google //구글 로그인
    private final UserService userService;
    @GetMapping("test")
    public ResponseEntity<String> loginCheck(Principal principal){
        return ResponseEntity.ok("로그인 성공, 유저이름 : "+ userService.findById(principal.getName()).getUsername());
    }
    @GetMapping("main")
    public ResponseEntity<String> mainPage(){
        //메인페이지 로그인 전에 시큐리티 권한확인하여 로그인상태를 구분,[ROLE_USER, ROLE_ADMIN, ROLE_ANONYMOUS]로 구분
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(Objects.equals(auth.getAuthorities().toString(), "ROLE_USER")){
            userService.checkInputTags(auth.getName());//유저가 분위기 및 장르, 좋아하는 아티스트를 넣었는지 확인하기
        }
        return ResponseEntity.ok("");
    }
}
