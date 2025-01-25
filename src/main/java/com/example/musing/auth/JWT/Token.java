package com.example.musing.auth.JWT;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "token")
@Entity
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
