package com.example.musing.board.controller;

import com.example.musing.board.dto.CreateBoardRequest;
import com.example.musing.board.dto.CreateBoardResponse;
import com.example.musing.board.dto.PostDto;
import com.example.musing.board.dto.UpdateBoardRequestDto;
import com.example.musing.board.entity.Board;
import com.example.musing.board.service.BoardService;
import com.example.musing.common.dto.ResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/musing/boards")
public class BoardController {

    private final BoardService boardService;


    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }


    @PostMapping(value ="/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto<Board> createPost(@ModelAttribute @Valid CreateBoardRequest request) {
        boardService.createBoard(request); // DTO를 Service로 전달
        return ResponseDto.of(null,"성공적으로 글이 작성되었습니다.");
    }


    @GetMapping(value ="/selectAll", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<CreateBoardResponse>> getAllBoards() {
//        List<CreateBoardResponse> responseList = boardService.getAllBoards();
//        return ResponseEntity.ok(responseList);
        return null;
    }

    @PutMapping(value ="/updatePost", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto<Board> updatePost(@RequestParam("boardId") Long boardId,@RequestBody UpdateBoardRequestDto updateRequest){
        boardService.updateBoard(boardId,updateRequest);
        return ResponseDto.of(null,"성공적으로 글이 수정되었습니다.");
    }

    @DeleteMapping("/deletePost")
    public ResponseDto<Board> deletePost(@RequestParam("boardId") Long boardId){
        boardService.deleteBoard(boardId);
        return ResponseDto.of(null,"성공적으로 글이 삭제되었습니다.");
    }
}
