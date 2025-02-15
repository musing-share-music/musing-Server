package com.example.musing.user.service;

import com.example.musing.user.dto.UserResponseDto;
import com.example.musing.user.entity.User;

import java.util.List;

public interface UserService {
    String checkInputTags(String userId);
    User findById(String userId);
    void saveGenres(String userid, List<Long> genres);
    void saveMoods(String userid, List<Long> moods);
    void saveArtists(String userid, List<String> artists);

    UserResponseDto.UserInfoDto getUserInfo(String userId);

    UserResponseDto.UserInfoPageDto getUserInfoPage(User user);
}
