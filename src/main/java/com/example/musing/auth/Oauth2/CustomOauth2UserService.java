package com.example.musing.auth.Oauth2;

import com.example.musing.auth.JWT.DTO.Oauth2Google;
import com.example.musing.entity.user.User;
import com.example.musing.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException{
        //유저 정보 가져오기
        Map<String,Object> userAttributes = super.loadUser(oAuth2UserRequest).getAttributes();

        //어느 소셜 로그인인지 구분하는 것(구글만 사용하기에 삭제 할수도 있음) //이건 어느 소셜로그인인지 구분하는 용이라 삭제예정
        String registationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        //구글 유저의 고유 ID값 가져오기 //이거도 삭제할수도있음
        String userNameAttributeName = oAuth2UserRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        //유저 DTO생성
        Oauth2Google oauth2Google = Oauth2Google.google(userAttributes);
        User user = saveUser(oauth2Google);

        return
    }
    private User saveUser(Oauth2Google oauth2Google){
        User user = userRepository.findByEmail(oauth2Google.email()). //유저 정보를 조회하고 없으면 저장
                orElseGet(oauth2Google::toEntity);
        return userRepository.save(user);
    }
}
