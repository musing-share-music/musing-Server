package com.example.musing.board.controller;

import com.example.musing.board.dto.BoardListRequestDto;
import com.example.musing.board.dto.CreateBoardRequest;
import com.example.musing.board.dto.UpdateBoardRequestDto;
import com.example.musing.board.entity.Board;
import com.example.musing.board.service.BoardService;
import com.example.musing.common.dto.ResponseDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping("/create")
    public ResponseDto<Board> createPost(@RequestBody @Valid CreateBoardRequest request) {
        boardService.createBoard(request); // DTO를 Service로 전달
        return ResponseDto.of(null, "성공적으로 글이 작성되었습니다.");
    }

    @GetMapping("/list")
    public ResponseDto<BoardListRequestDto.BoardListDto> BoardListPage() {
        BoardListRequestDto.BoardListDto boardList = boardService.findBoardList();
        return ResponseDto.of(boardList);
    }

    @GetMapping("/list/page")
    public ResponseDto<Page<BoardListRequestDto.BoardDto>> getBoards(
            @RequestParam(name = "page", defaultValue = "1") int page) {
        Page<BoardListRequestDto.BoardDto> responseList = boardService.findBoardDto(page);
        return ResponseDto.of(responseList);
    }

    @GetMapping("/list/page/search")
    public ResponseDto<Page<BoardListRequestDto.BoardDto>> getBoardsByKeyword(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "searchType") String searchType,
            @RequestParam(name = "keyword") String keyword) {
        Page<BoardListRequestDto.BoardDto> responseList = boardService.search(page, searchType, keyword);
        return ResponseDto.of(responseList);
    }

    @PutMapping("/updatePost")
    public ResponseDto<Board> updatePost(@RequestParam("boardId") Long boardId, @RequestBody UpdateBoardRequestDto updateRequest) {
        boardService.updateBoard(boardId, updateRequest);
        return ResponseDto.of(null, "성공적으로 글이 수정되었습니다.");
    }

    @DeleteMapping("/deletePost")
    public ResponseDto<Board> updatePost(@RequestParam("boardId") Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseDto.of(null, "성공적으로 글이 삭제되었습니다.");
    }
}
