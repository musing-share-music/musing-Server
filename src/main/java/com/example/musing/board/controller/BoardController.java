package com.example.musing.board.controller;

import com.example.musing.board.dto.CreateBoardRequest;
import com.example.musing.board.dto.CreateBoardResponse;
import com.example.musing.board.dto.PostDto;
import com.example.musing.board.entity.Board;
import com.example.musing.board.service.BoardService;
import com.example.musing.common.dto.ResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;


    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }


    @PostMapping("/create")
    public ResponseDto<Board> createPost(@RequestBody @Valid CreateBoardRequest request) {
        boardService.createBoard(request); // DTO를 Service로 전달
        return ResponseDto.of(null,"성공적으로 글이 작성되었습니다.");
    }


    @GetMapping("/selectAll")
    public ResponseEntity<List<CreateBoardResponse>> getAllBoards() {
        List<CreateBoardResponse> responseList = boardService.getAllBoards();
        return ResponseEntity.ok(responseList);
    }
}
