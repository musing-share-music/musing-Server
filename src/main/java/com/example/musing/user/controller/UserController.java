package com.example.musing.user.controller;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.common.dto.ResponseDto;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.genre.service.GenreService;
import com.example.musing.mood.dto.MoodDto;
import com.example.musing.mood.service.MoodService;
import com.example.musing.user.dto.UserResponseDto;
import com.example.musing.user.entity.User;
import com.example.musing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/musing/user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final GenreService genreService;
    private final MoodService moodService;

    @GetMapping
    public ResponseDto<UserResponseDto.UserInfoPageDto> getUserInfoPage() {
        return ResponseDto.of(userService.getUserInfoPage(getUserId()));
    }

    @GetMapping("/like/genre")
    public ResponseDto<List<GenreDto>> selectGenreForm() {
        return ResponseDto.of(genreService.getGenreDtos(), "");
    }

    @PostMapping("/genre")
    public ResponseDto<List<GenreDto>> updateGenres(@RequestBody List<Long> chooseGenres) {
        return ResponseDto.of(userService.updateGenres(getUserId(), chooseGenres));
    }

    @GetMapping("/like/mood")
    public ResponseDto<List<MoodDto>> selectMoodForm() {
        return ResponseDto.of(moodService.getMoodDtos(), "");
    }

    @PostMapping("/mood")
    public ResponseDto<List<MoodDto>> updateMoods(@RequestBody List<Long> chooseMoods) {
        return ResponseDto.of(userService.updateMoods(getUserId(), chooseMoods));
    }

    @PostMapping("/artist")
    public ResponseDto<List<ArtistDto>> updateArtists(@RequestBody(required = false) List<String> chooseArtist) {
        return ResponseDto.of(userService.updateArtists(getUserId(), chooseArtist));
    }

    private User getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName().equals("anonymousUser")) {
            return null;
        }

        return userService.findById(authentication.getName());
    }
}
