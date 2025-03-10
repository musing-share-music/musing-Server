package com.example.musing.board.controller;

import com.example.musing.board.dto.BoardListResponseDto;
import com.example.musing.board.dto.CreateBoardRequest;
import com.example.musing.board.dto.DetailResponse;
import com.example.musing.board.dto.UpdateBoardRequestDto;
import com.example.musing.board.entity.Board;
import com.example.musing.board.service.BoardService;
import com.example.musing.common.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/musing/board")
public class BoardController {

    private final BoardService boardService;

    @PostMapping(value ="/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto<Board> createPost(@ModelAttribute @Valid CreateBoardRequest request,
                                         @RequestPart(value = "image", required = false) List<MultipartFile> images) {

        if (images == null || images.isEmpty()) {
            images = new ArrayList<>();  // 이미지 없음으로 처리하거나, 기본값 설정
        }

        boardService.createBoard(request,images); // DTO를 Service로 전달
        return ResponseDto.of(null,"성공적으로 글이 작성되었습니다.");
    }


    @GetMapping("/list")
    public ResponseDto<BoardListResponseDto.BoardListDto> BoardListPage() {
        BoardListResponseDto.BoardListDto boardList = boardService.findBoardList();
        return ResponseDto.of(boardList);
    }

    @GetMapping("/list/page")
    public ResponseDto<Page<BoardListResponseDto.BoardDto>> getBoards(
            @RequestParam(name = "page", defaultValue = "1") int page) {
        Page<BoardListResponseDto.BoardDto> responseList = boardService.findBoardDto(page);
        return ResponseDto.of(responseList);
    }

    @GetMapping("/list/page/search")
    public ResponseDto<Page<BoardListResponseDto.BoardDto>> getBoardsByKeyword(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "searchType") String searchType,
            @RequestParam(name = "keyword") String keyword) {
        Page<BoardListResponseDto.BoardDto> responseList = boardService.search(page, searchType, keyword);
        return ResponseDto.of(responseList);
    }

    @PutMapping(value ="/updatePost", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto<Board> updatePost(@PathVariable long boardId, @ModelAttribute @Valid UpdateBoardRequestDto updateRequest,
                                         @RequestPart(required = false) List<String> deleteFileLinks,
                                         @RequestPart(required = false) List<MultipartFile> files) {

        boardService.updateBoard(boardId, updateRequest, deleteFileLinks, files);
        return ResponseDto.of(null,"성공적으로 글이 수정되었습니다.");
    }


    @DeleteMapping("/deletePost")
    public ResponseDto<Board> deletePost(@RequestParam("boardId") Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseDto.of(null, "성공적으로 글이 삭제되었습니다.");
    }

    @GetMapping("/selectDetail")
    public ResponseDto<DetailResponse> selectDetail(@RequestParam("boardId") Long boardId) {
        DetailResponse responseDto = boardService.selectDetail(boardId);
        return ResponseDto.of(responseDto, "글 조회에 성공했습니다.");
    }
}
