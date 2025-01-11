package com.example.musing.board.service;

import com.example.musing.board.dto.*;
import com.example.musing.main.dto.MainPageBoardDto;
import org.mortbay.log.Log;

import java.util.List;

public interface BoardService {
    List<GenreBoardDto> findBy5GenreBoard(String genre);

    BoardDto findHotMusicBoard();

    List<MainPageBoardDto> findBy5Board();

    List<GenreBoardDto> findBy10LikeMusics(String userId);

    //게시판 등록 로직
    void createBoard(CreateBoardRequest request);
    //전체 글 조회 로직
    List<CreateBoardResponse> getAllBoards();
    // 글 삭제
    void deleteBoard(Long boardId);

    //글 수정
    void updateBoard(Long boardId,UpdateBoardRequestDto updateRequest);
}
