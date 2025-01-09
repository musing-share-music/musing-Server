package com.example.musing.board.service;

import com.example.musing.board.dto.BoardDto;
import com.example.musing.board.dto.CreateBoardRequest;
import com.example.musing.board.dto.GenreBoardDto;
import com.example.musing.board.dto.PostDto;
import com.example.musing.board.entity.Board;
import com.example.musing.main.dto.MainPageBoardDto;
import com.example.musing.user.entity.User;

import java.util.List;

public interface BoardService {
    List<GenreBoardDto> findBy5GenreBoard(String genre);

    BoardDto findHotMusicBoard();

    List<MainPageBoardDto> findBy5Board();

    List<GenreBoardDto> findBy10LikeMusics(String userId);
}
