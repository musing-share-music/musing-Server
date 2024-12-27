package com.example.musing.board.service;

import com.example.musing.board.dto.GenreBoardDto;
import com.example.musing.board.dto.HotMusicBoardDto;
import com.example.musing.board.entity.Board;
import com.example.musing.main.dto.MainPageBoardDto;

import java.util.List;
import java.util.Optional;

public interface BoardService {
    GenreBoardDto entityToDto_Genre(Board board);
    List<GenreBoardDto> entitysToDtos(String genre);
    List<Board> findByTop5BoardGenre(String genre);
    Optional<Board> findHotMusicBoard();
    HotMusicBoardDto entityToDto_HotBoard(Board board);
    List<Board> findByTop5Board();

    MainPageBoardDto entityToDto(Board board);
    List<MainPageBoardDto> entitysToDtos();
}
