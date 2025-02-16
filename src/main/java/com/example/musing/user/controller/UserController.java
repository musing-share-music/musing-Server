package com.example.musing.user.controller;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.common.dto.ResponseDto;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.mood.dto.MoodDto;
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

    @GetMapping
    public ResponseDto<UserResponseDto.UserInfoPageDto> getUserInfoPage() {
        return ResponseDto.of(userService.getUserInfoPage(getUserId()));
    }

    @PostMapping("/genre")
    public ResponseDto<List<GenreDto>> updateGenres(@RequestBody List<Long> chooseGenres) {
        return ResponseDto.of(userService.updateGenres(getUserId(), chooseGenres));
    }

    @PostMapping("/mood")
    public ResponseDto<List<MoodDto>> updateMoods(@RequestBody List<Long> chooseMoods) {
        return ResponseDto.of(userService.updateMoods(getUserId(), chooseMoods));
    }

    @PostMapping("/artist")
    public ResponseDto<List<ArtistDto>> updateArtists(@RequestBody List<String> chooseArtist) {
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
