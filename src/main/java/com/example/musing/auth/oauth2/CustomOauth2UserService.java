package com.example.musing.auth.oauth2;

import com.example.musing.auth.jwt.dto.Oauth2Google;
import com.example.musing.auth.jwt.dto.PrincipalDetails;
import com.example.musing.common.utils.youtube.YoutubeService;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOauth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final YoutubeService youtubeService;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        Map<String, Object> userAttributes = new HashMap<>(oAuth2User.getAttributes());

        if ("google".equals(oAuth2UserRequest.getClientRegistration().getRegistrationId())) {
            String accessToken = oAuth2UserRequest.getAccessToken().getTokenValue();
            String youtubeChannelId = youtubeService.getYoutubeChannelId(accessToken);
            userAttributes.put("youtubeChannelId", youtubeChannelId);
        }

        Oauth2Google oauth2Google = Oauth2Google.google(userAttributes);

        log.info("===getAttributes()===: " + userAttributes);
        User user = saveUser(oauth2Google);
        return new PrincipalDetails(user, userAttributes);
    }

    @Transactional
    private User saveUser(Oauth2Google oauth2Google) {
        User user = userRepository.findByEmail(oauth2Google.email()).orElseGet(oauth2Google::toEntity);
        //구글 닉네임 또는 프로필, YoutubeId 확인을 하여, 변경 시 적용하기 위해 추가
        if (!Objects.equals(user.getUsername(), oauth2Google.name()) ||
                !Objects.equals(user.getProfile(), oauth2Google.profile()) ||
                !Objects.equals(user.getYoutubeId(), oauth2Google.youtubeId())) {

            user.updateGoogleInfo(oauth2Google.name(), oauth2Google.profile(), oauth2Google.youtubeId());
        }
        log.info("유저 정보를 확인했습니다 " + user.getUsername());
        return userRepository.save(user);
    }
}
