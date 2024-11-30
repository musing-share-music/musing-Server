package com.example.musing.auth;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Token {
    @Id
    private String tokenid;

    private String accesstoken;
    private String refreshtoken;

    //리프래쉬 토큰 재발급용
    public Token updateRefreshToken(String refreshtoken){
        this.refreshtoken = refreshtoken;
        return this;
    }//
    //엑세스 토큰 갱신
    public void updateAccessToken(String accesstoken){
        this.accesstoken = accesstoken;
    }
}
