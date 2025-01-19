package com.example.musing.main.service;

import com.example.musing.board.dto.HotBoardDto;
import com.example.musing.board.dto.GenreBoardDto;
import com.example.musing.board.service.BoardService;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.genre.service.GenreService;
import com.example.musing.main.dto.LoginMainPageDto;
import com.example.musing.main.dto.MainPageBoardDto;
import com.example.musing.main.dto.NotLoginMainPageDto;
import com.example.musing.notice.dto.NoticeDto;
import com.example.musing.notice.service.NoticeService;
import com.example.musing.user.entity.User_LikeGenre;
import com.example.musing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MainServiceImpl implements MainService {
    private final NoticeService noticeService;
    private final BoardService boardService;
    private final UserService userService;
    private final GenreService genreService;
    @Override
    public NotLoginMainPageDto notLoginMainPage(String modalCheck) { //로그인 하지않는 메인페이지

        NoticeDto noticeDto = noticeService.findNotice(); //최신 공지사항 가져오기

        //랜덤 장르를 고르고 최신순의 게시글을 최대5개 가져옴
        GenreDto recommendGenre = genreService.getRandomGenre();
        List<GenreBoardDto> genreBoardDtos = boardService.findBy5GenreBoard(recommendGenre.genreName());

        HotBoardDto hotBoardDto = boardService.findHotMusicBoard(); //핫한 게시글


        //최신 게시글 5개 가져오기
        List<MainPageBoardDto> mainPageBoardDtos = boardService.findBy5Board();

        return NotLoginMainPageDto.of(noticeDto, recommendGenre, genreBoardDtos, hotBoardDto, mainPageBoardDtos, modalCheck);
    }

    @Override
    public LoginMainPageDto LoginMainPage(String userId, String modalCheck) {

        NoticeDto noticeDto = noticeService.findNotice(); //최신 공지사항 가져오기

        List<GenreDto> likeGenre = userService.findById(userId).getGenres()
                .stream()
                .map(User_LikeGenre::getGenre)
                .map(GenreDto::toDto)
                .toList(); //좋아하는 장르

        //좋아요한 음악
        List<GenreBoardDto> likeMusic = boardService.findBy10LikeMusics(userId);

        //랜덤 장르를 고르고 최신순의 게시글을 최대5개 가져옴
        GenreDto recommendGenre = genreService.getRandomGenre();
        List<GenreBoardDto> genreBoardDtos = boardService.findBy5GenreBoard(recommendGenre.genreName());

        HotBoardDto hotBoardDto = boardService.findHotMusicBoard(); //핫한 게시글

        //최신 게시글 5개 가져오기
        List<MainPageBoardDto> mainPageBoardDtos = boardService.findBy5Board();

        return LoginMainPageDto.of(noticeDto,likeGenre,likeMusic,
                recommendGenre,genreBoardDtos,hotBoardDto,mainPageBoardDtos ,modalCheck);
    }

    @Override
    public List<GenreBoardDto> selcetGenre(String genre) {
        return boardService.findBy5GenreBoard(genre);
    }
}
