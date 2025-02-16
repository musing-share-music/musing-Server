package com.example.musing.user.controller;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.common.dto.ResponseDto;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.mood.dto.MoodDto;
import com.example.musing.user.dto.UserResponseDto;
import com.example.musing.user.entity.User;
import com.example.musing.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "유저 회원 정보 조회 관련 도메인")
@RequestMapping("/musing/user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @Operation(summary = "유저 정보 조회",
            description = "유저 이름 및 이메일, 자신이 선택한 모달류(장르, 분위기, 아티스트)와 작성한 게시글 및 리뷰")
    @GetMapping
    public ResponseDto<UserResponseDto.UserInfoPageDto> getUserInfoPage() {
        return ResponseDto.of(userService.getUserInfoPage(getUserId()));
    }

    @Operation(summary = "유저가 선호하는 장르 수정",
            description = "유저가 선호하는 장르의 수정된 결과의 Id값을 리스트로 주면 됩니다.<br>" +
                    "삭제하고 추가한 것을 구분없이 전체 결과로 보내면 됩니다.")
    @PostMapping("/genre")
    public ResponseDto<List<GenreDto>> updateGenres(@RequestBody List<Long> chooseGenres) {
        return ResponseDto.of(userService.updateGenres(getUserId(), chooseGenres));
    }

    @Operation(summary = "유저가 선호하는 분위기 수정",
            description = "유저가 선호하는 분위기의 수정된 결과의 Id값을 리스트로 주면 됩니다.<br>" +
                    "삭제하고 추가한 것을 구분없이 전체 결과로 보내면 됩니다.")
    @PostMapping("/mood")
    public ResponseDto<List<MoodDto>> updateMoods(@RequestBody List<Long> chooseMoods) {
        return ResponseDto.of(userService.updateMoods(getUserId(), chooseMoods));
    }

    @Operation(summary = "유저가 선호하는 가수 수정",
            description = "유저가 선호하는 가수의 수정된 결과의 String값을 리스트로 주면 됩니다.<br>" +
                    "삭제하고 추가한 것을 구분없이 전체 결과로 보내면 됩니다.")
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
