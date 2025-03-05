package com.example.musing.auth.jwt;

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

    private String ipAdress;

    private String userAgent;

    private String refreshtoken;

    //리프래쉬 토큰 재발급용
    public Token updateRefreshToken(String ipAdress, String userAgent, String refreshtoken){
        this.ipAdress = ipAdress;
        this.userAgent = userAgent;
        this.refreshtoken = refreshtoken;
        return this;
    }
}
