package com.example.musing.board.controller;

import com.example.musing.board.dto.BoardDto;
import com.example.musing.board.dto.CreateBoardRequest;
import com.example.musing.board.dto.PostDto;
import com.example.musing.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;


    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }


    @PostMapping("/create")
    public ResponseEntity<PostDto> createMusicBoard(@RequestBody CreateBoardRequest request) {
        PostDto boardDto = boardService.createBoard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(boardDto);
    }


}
