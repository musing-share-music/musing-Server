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
        Optional<User> user;
        user = userRepository.findByEmail(oauth2Google.email());
        //최초 로그인 이후 구글 계정의 정보 수정 시 갱신을 위해 저장과 같이 둠
        if(user.isPresent()){
            user = Optional.of(oauth2Google.toEntity()); //유저 정보가 있으면 Optional타입에서 User로 변환, 다시 빌드하여 변경된 부분을 수정하고 저장
            log.info("유저 정보를 확인했습니다 " + user.get().getUsername());
            return userRepository.save(user.get());
        }else{
            User userObject = oauth2Google.toEntity(); //유저 정보가 없다면 새로운 계정 저장

            log.info("구글 계정을 통해 회원가입이 되었습니다. " + userObject.getUsername());
            return userRepository.save(userObject);
        }
    }
}
