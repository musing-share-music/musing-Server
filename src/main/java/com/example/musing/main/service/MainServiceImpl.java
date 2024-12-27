package com.example.musing.main.service;

import com.example.musing.board.dto.BoardDto;
import com.example.musing.board.entity.Board;
import com.example.musing.board.service.BoardService;
import com.example.musing.main.dto.LoginMainPageDto;
import com.example.musing.main.dto.NotLoginMainPageDto;
import com.example.musing.notice.dto.NoticeDto;
import com.example.musing.notice.entity.Notice;
import com.example.musing.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {
    private final NoticeService noticeService;
    private final BoardService boardService;
    final String[] gernes = {"K-POP", "J-POP", "클래식", "발라드", "얼터너티브", "인디", "디스코", "록", "메탈", "신디팝",
            "R&B", "뉴웨이브", "포크", "컨트리", "블루스", "일렉트로닉", "트로트", "OST", "CCM", "뮤지컬", "EDM", "슈게이징"};

    private String randomGenre(String[] gernes) {//랜덤한 장르를 뽑는 용
        Random random = new Random();
        int index = random.nextInt(gernes.length);
        return gernes[index];
    }

    @Override
    @Transactional
    public NotLoginMainPageDto notLoginMainPage() { //로그인 하지않는 메인페이지
        Optional<Notice> notice = noticeService.findNotice(); //최신 공지사항 가져오기
        NoticeDto noticeDto = null; //공지사항

        if (notice.isPresent()) {
            noticeDto = noticeService.entityToDto(notice.get());
        }

        //랜덤 장르를 고르고 최신순의 게시글을 최대5개 가져옴
        List<BoardDto.GenreBoardDto> genreBoardDtos = boardService.entitysToDtos(randomGenre(gernes));

        BoardDto.HotMusicBoardDto hotMusicBoardDto = null; //핫한 게시글
        Optional<Board> hotBoard = boardService.findHotMusicBoard(); //핫한 게시글 가져오기
        if (hotBoard.isPresent()) {
            hotMusicBoardDto = boardService.entityToDto_HotBoard(hotBoard.get());
        }

        //최신 게시글 5개 가져오기
        List<BoardDto.MainPageBoardDto> mainPageBoardDtos = boardService.entitysToDtos();

        return NotLoginMainPageDto.of(noticeDto, genreBoardDtos, hotMusicBoardDto, mainPageBoardDtos);
    }

    @Override
    public LoginMainPageDto LoginMainPage() {
        return null;
    }
}
