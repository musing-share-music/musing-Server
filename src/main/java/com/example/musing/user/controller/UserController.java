package com.example.musing.user.controller;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.board.dto.BoardListResponseDto;
import com.example.musing.common.dto.ResponseDto;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.genre.service.GenreService;
import com.example.musing.mood.dto.MoodDto;
import com.example.musing.mood.service.MoodService;
import com.example.musing.reply.dto.ReplyResponseDto;
import com.example.musing.user.dto.UserResponseDto;
import com.example.musing.user.entity.User;
import com.example.musing.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Tag(name = "유저 회원 정보 조회 관련 도메인")
@RequestMapping("/musing/user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final GenreService genreService;
    private final MoodService moodService;

    @GetMapping("/withdraw")
    public ResponseDto<String> deactivateUser(HttpServletResponse response) {
        userService.withdraw(response);
        return ResponseDto.of("회원 탈퇴에 성공했습니다.");
    }

    @GetMapping("/my-reply")
    public ResponseDto<Page<ReplyResponseDto.MyReplyDto>> selectMyReply(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "DESC") String sort) {
        return ResponseDto.of(userService.getMyReply(getUser(), page, sort));
    }

    @GetMapping("/my-reply/search")
    public ResponseDto<Page<ReplyResponseDto.MyReplyDto>> selectMyReplySearch(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam(defaultValue = "content") String searchType,
            @RequestParam(defaultValue = "") String keyword) {
        return ResponseDto.of(userService.getMyReplySearch(getUser(), page, sort, searchType, keyword));
    }

    @GetMapping("/my-board")
    public ResponseDto<Page<BoardListResponseDto.BoardRecapDto>> selectMyBoard(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "DESC") String sort) {
        return ResponseDto.of(userService.getMyBoard(getUser(), page, sort));
    }

    @GetMapping("/my-board/search")
    public ResponseDto<Page<BoardListResponseDto.BoardRecapDto>> selectMyBoardSearch(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam(defaultValue = "title") String searchType,
            @RequestParam(defaultValue = "") String keyword) {
        return ResponseDto.of(userService.getMyBoardSearch(getUser(), page, sort, searchType, keyword));
    }

    @Operation(summary = "유저 정보 조회",
            description = "유저 이름 및 이메일, 자신이 선택한 모달류(장르, 분위기, 아티스트)와 작성한 게시글 및 리뷰")
    @GetMapping
    public ResponseDto<UserResponseDto.UserInfoPageDto> getUserInfoPage() {
        return ResponseDto.of(userService.getUserInfoPage(getUser()));
    }

    @GetMapping("/genre")
    public ResponseDto<List<GenreDto>> selectGenreForm() {
        return ResponseDto.of(genreService.getGenreDtos());
    }

    @PostMapping("/like/genre")
    public ResponseDto<List<GenreDto>> updateGenres(@RequestBody List<Long> chooseGenres) {
        return ResponseDto.of(userService.updateGenres(getUser(), chooseGenres));
    }

    @GetMapping("/mood")
    public ResponseDto<List<MoodDto>> selectMoodForm() {
        return ResponseDto.of(moodService.getMoodDtos());
    }

    @PostMapping("/like/mood")
    public ResponseDto<List<MoodDto>> updateMoods(@RequestBody List<Long> chooseMoods) {
        return ResponseDto.of(userService.updateMoods(getUser(), chooseMoods));
    }

    @PostMapping("/like/artist")
    public ResponseDto<List<ArtistDto>> updateArtists(@RequestBody(required = false) List<String> chooseArtist) {
        return ResponseDto.of(userService.updateArtists(getUser(), chooseArtist));
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName().equals("anonymousUser")) {
            return null;
        }

        return userService.findById(authentication.getName());
    }
}
