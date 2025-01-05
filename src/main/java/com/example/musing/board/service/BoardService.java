package com.example.musing.board.service;

import com.example.musing.board.dto.BoardDto;
import com.example.musing.board.dto.CreateBoardRequest;
import com.example.musing.board.dto.GenreBoardDto;
<<<<<<< HEAD
import com.example.musing.board.dto.PostDto;
=======
import com.example.musing.board.entity.Board;
>>>>>>> f7249532da7804cdadecb0a80672baf6e0627657
import com.example.musing.main.dto.MainPageBoardDto;

import java.util.List;

public interface BoardService {
    List<GenreBoardDto> findBy5GenreBoard(String genre);

    BoardDto findHotMusicBoard();

    List<MainPageBoardDto> findBy5Board();

    List<BoardDto> findBy10LikeMusics(String userId);

    PostDto createBoard(CreateBoardRequest request);


}
