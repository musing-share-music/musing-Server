package com.example.musing.main.service;

import com.example.musing.board.dto.BoardDto;
import com.example.musing.board.dto.GenreBoardDto;
import com.example.musing.board.service.BoardService;
import com.example.musing.main.dto.LoginMainPageDto;
import com.example.musing.main.dto.MainPageBoardDto;
import com.example.musing.main.dto.NotLoginMainPageDto;
import com.example.musing.notice.dto.NoticeDto;
import com.example.musing.notice.service.NoticeService;
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

    final String[] gernes = {"K-POP", "J-POP", "클래식", "발라드", "얼터너티브", "인디", "디스코", "록", "메탈", "신디팝",
            "R&B", "뉴웨이브", "포크", "컨트리", "블루스", "일렉트로닉", "트로트", "OST", "CCM", "뮤지컬", "EDM", "슈게이징"};

    @Override
    public NotLoginMainPageDto notLoginMainPage(String modalCheck) { //로그인 하지않는 메인페이지

        NoticeDto noticeDto = noticeService.findNotice(); //최신 공지사항 가져오기

        //랜덤 장르를 고르고 최신순의 게시글을 최대5개 가져옴
        String recommendGenreName = randomGenre(gernes);
        List<GenreBoardDto> genreBoardDtos = boardService.findBy5GenreBoard(randomGenre(gernes));

        BoardDto hotBoardDto = boardService.findHotMusicBoard(); //핫한 게시글


        //최신 게시글 5개 가져오기
        List<MainPageBoardDto> mainPageBoardDtos = boardService.findBy5Board();

        return NotLoginMainPageDto.of(noticeDto, recommendGenreName, genreBoardDtos, hotBoardDto, mainPageBoardDtos, modalCheck);
    }

    @Override
    public LoginMainPageDto LoginMainPage(String userId, String modalCheck) {

        NoticeDto noticeDto = noticeService.findNotice(); //최신 공지사항 가져오기

        String likeGenreStr = userService.findById(userId).getLikegenre(); //좋아하는 장르
        // 문자열에서 불필요한 대괄호 제거 및 나누기
        List<String> likeGenre = toList(likeGenreStr);

        //좋아요한 음악
        List<GenreBoardDto> likeMusic = boardService.findBy10LikeMusics(userId);

        //랜덤 장르를 고르고 최신순의 게시글을 최대5개 가져옴
        String recommendGenreName = randomGenre(gernes);
        List<GenreBoardDto> genreBoardDtos = boardService.findBy5GenreBoard(randomGenre(gernes));

        BoardDto hotBoardDto = boardService.findHotMusicBoard(); //핫한 게시글

        //최신 게시글 5개 가져오기
        List<MainPageBoardDto> mainPageBoardDtos = boardService.findBy5Board();

        return LoginMainPageDto.of(noticeDto,likeGenre,likeMusic,recommendGenreName,genreBoardDtos,hotBoardDto,mainPageBoardDtos ,modalCheck);
    }

    @Override
    public List<GenreBoardDto> selcetGenre(String genre) {
        return boardService.findBy5GenreBoard(genre);
    }

    private String randomGenre(String[] gernes) {//랜덤한 장르를 뽑는 용
        Random random = new Random();
        int index = random.nextInt(gernes.length);
        return gernes[index];
    }

    private List<String> toList(String string) {
        return Arrays.asList(
                string.replace("[", "") // 왼쪽 대괄호 제거
                        .replace("]", "") // 오른쪽 대괄호 제거
                        .replace("\"", "") // 큰따옴표 제거
                        .split(", ")       // 쉼표 기준으로 나누기
        );
    }
}
