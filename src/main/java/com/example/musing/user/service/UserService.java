package com.example.musing.user.service;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.mood.dto.MoodDto;
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

    List<GenreDto> updateGenres(User user, List<Long> chooseGenres);
    List<MoodDto> updateMoods(User user, List<Long> chooseMoods);
    List<ArtistDto> updateArtists(User user, List<String> choosertists);
}
