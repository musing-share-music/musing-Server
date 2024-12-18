package com.example.musing.user.dto;

import lombok.Data;

@Data
public class UserMainFormDto {
    private String username;
    private String email;
    private String likeGenre;
    private int likeMusicCount;
    private int playListCount;

}
