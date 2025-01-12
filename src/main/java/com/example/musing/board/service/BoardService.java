package com.example.musing.board.service;

import com.example.musing.board.dto.*;
import com.example.musing.main.dto.MainPageBoardDto;
import org.springframework.data.domain.Page;
import org.mortbay.log.Log;

import java.util.List;

public interface BoardService {
    List<GenreBoardDto> findBy5GenreBoard(String genre);

    HotBoardDto findHotMusicBoard();

    List<MainPageBoardDto> findBy5Board();

    List<GenreBoardDto> findBy10LikeMusics(String userId);

    //게시판 등록 로직
    void createBoard(CreateBoardRequest request);

    BoardRequestDto.BoardListDto findBoardList();
    Page<BoardRequestDto.BoardDto> findBoardDto(int page);

    Page<BoardRequestDto.BoardDto> search(int page, String searchType, String keyword);
    // 글 삭제
    void deleteBoard(Long boardId);
    //글 수정
    void updateBoard(Long boardId,UpdateBoardRequestDto updateRequest);
}
