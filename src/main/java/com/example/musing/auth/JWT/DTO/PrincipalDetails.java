package com.example.musing.auth.JWT.DTO;

import com.example.musing.entity.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public record PrincipalDetails(
        User user,
        Map<String,Object> attributes
        ) implements OAuth2User {

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;//이 값 예시  https://developers.google.com/identity/openid-connect/openid-connect?hl=ko#an-id-tokens-payload
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().getKey()));
    }

    @Override
    public String getName() {
        return attributes.get("sub").toString(); //구글은 필드값 이름이 sub
    }
}
