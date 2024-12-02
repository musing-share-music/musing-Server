package com.example.musing.auth.JWT.DTO;

import com.example.musing.entity.user.User;
import lombok.Builder;

import java.util.Map;

@Builder
public record Oauth2Google (
    String name,
    String email,
    String profile){ //프로필 사진 링크

    public static Oauth2Google google(Map<String,Object> attributes){
        return Oauth2Google.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profile((String) attributes.get("profile")).build();
        //이름,이메일,프로필 이미지 링크
    }

    public User toEntity(){
        return User.builder()
                .profile(profile)
                .username(name)
                .email(email).build();
    }
}

