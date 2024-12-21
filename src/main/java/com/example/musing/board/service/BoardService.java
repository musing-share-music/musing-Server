package com.example.musing.board.service;

import com.example.musing.board.dto.BoardDto;
import com.example.musing.board.entity.Board;

import java.util.List;
import java.util.Optional;

public interface BoardService {
    BoardDto.GenreBoardDto entityToDto_Genre(Board board);
    List<BoardDto.GenreBoardDto> entitysToDtos(String genre);
    List<Board> findByTop5BoardGenre(String genre);
    Optional<Board> findHotMusicBoard();
    BoardDto.HotMusicBoardDto entityToDto_HotBoard(Board board);
    List<Board> findByTop5Board();

    BoardDto.MainPageBoardDto entityToDto(Board board);
    List<BoardDto.MainPageBoardDto> entitysToDtos();
}
