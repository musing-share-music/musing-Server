package com.example.musing.user.service;

import com.example.musing.user.entity.User;

public interface UserService {
    String checkInputTags(String userId);
    User findById(String userId);
    void saveGenres(String userid, String genres);
    void saveMoods(String userid, String moods);
    void saveArtists(String userid, String artists);
}
