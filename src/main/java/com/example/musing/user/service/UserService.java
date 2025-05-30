package com.example.musing.user.service;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.board.dto.BoardListResponseDto;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.mood.dto.MoodDto;
import com.example.musing.reply.dto.ReplyResponseDto;
import com.example.musing.user.dto.UserResponseDto;
import com.example.musing.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

public interface UserService {
    void withdraw(HttpServletResponse response) throws IOException, InterruptedException;
    String checkInputTags(String userId);
    User findById(String userId);
    void saveGenres(String userid, List<Long> genres);
    void saveMoods(String userid, List<Long> moods);
    void saveArtists(String userid, List<String> artists);

    UserResponseDto.UserInfoDto getUserInfo(String userId);

    UserResponseDto.UserInfoPageDto getUserInfoPage(User user);

    Page<BoardListResponseDto.BoardRecapDto> getMyBoard(User user, int page, String sort);
    Page<BoardListResponseDto.BoardRecapDto> getMyBoardSearch(
            User user, int page, String sort, String searchType, String keyword);
    Page<ReplyResponseDto.MyReplyDto> getMyReply(User user, int page, String sort);
    Page<ReplyResponseDto.MyReplyDto> getMyReplySearch(User user, int page, String sort, String searchType, String keyword);
    List<GenreDto> updateGenres(User user, List<Long> chooseGenres);
    List<MoodDto> updateMoods(User user, List<Long> chooseMoods);
    List<ArtistDto> updateArtists(User user, List<String> choosertists);
}
