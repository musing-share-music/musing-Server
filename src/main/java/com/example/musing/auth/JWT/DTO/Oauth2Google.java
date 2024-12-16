package com.example.musing.auth.JWT.DTO;



import com.example.musing.user.entity.User;
import lombok.Builder;

import java.util.Map;

@Builder
public record Oauth2Google (
    String id,
    String name,
    String email,
    String profile){ //프로필 사진 링크

    //나중에 유튜브 계정 id랑 구글 sub일치하는지 확인해보고 빌더해서 값 저장할듯함, GeneratedValue 대신
    
    public static Oauth2Google google(Map<String,Object> attributes){
        return Oauth2Google.builder()
                .id((String) attributes.get("sub"))
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profile((String) attributes.get("picture")).build();

        //이름,이메일,프로필 이미지 링크
    }

    public User toEntity(){
        return User.builder()
                .id(id)
                .profile(profile)
                .username(name)
                .email(email).build();
    }
}

