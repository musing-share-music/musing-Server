package com.example.musing.auth.Oauth2;

import com.example.musing.auth.JWT.DTO.Oauth2Google;
import com.example.musing.auth.JWT.DTO.PrincipalDetails;


import com.example.musing.user.entity.User;
import com.example.musing.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOauth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException{
        //유저 정보 가져오기
        Map<String,Object> userAttributes = super.loadUser(oAuth2UserRequest).getAttributes();

        //유저 DTO생성
        Oauth2Google oauth2Google = Oauth2Google.google(userAttributes);
        log.info("===getAttributes()===: " + userAttributes);
        User user = saveUser(oauth2Google);
        //OAuth2User 반환
        return new PrincipalDetails(user,userAttributes);
    }
    @Transactional
    private User saveUser(Oauth2Google oauth2Google){
        User user = userRepository.findByEmail(oauth2Google.email()).orElseGet(oauth2Google::toEntity);
        //구글 닉네임 또는 프사 변경 시 적용하기 위해 추가
        if(!user.getUsername().equals(oauth2Google.name())||!user.getProfile().equals(oauth2Google.profile())){
            user.updateGoogleInfo(oauth2Google.name(),oauth2Google.profile());
        }
        log.info("유저 정보를 확인했습니다 " + user.getUsername());
        return userRepository.save(user);
    }
}
