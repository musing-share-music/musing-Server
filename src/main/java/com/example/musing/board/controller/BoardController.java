package com.example.musing.board.controller;

import com.example.musing.alarm.service.AlarmService;
import com.example.musing.board.dto.*;
import com.example.musing.board.entity.Board;
import com.example.musing.board.service.BoardService;
import com.example.musing.common.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.example.musing.alarm.entity.AlarmType.APPLYPERMIT;

@RequiredArgsConstructor
@RestController
@RequestMapping("/musing/board")
public class BoardController {

    private final BoardService boardService;

    @PostMapping(value ="/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto<Void> createPost(@ModelAttribute @Valid CreateBoardRequest request,
                                         @RequestPart(value = "image", required = false) List<MultipartFile> images) {

        if (images == null || images.isEmpty()) {
            images = new ArrayList<>();  // 이미지 없음으로 처리하거나, 기본값 설정
        }

        boardService.createBoard(request,images); // DTO를 Service로 전달

        return ResponseDto.of(null,"성공적으로 글이 작성되었습니다.");
    }

    @Operation(summary = "음악 추천 게시판 리스트",
            description = "음악 추천 게시판으로 상단 팝업창 데이터와 하단 페이지를 같이 조회합니다.")
    public ResponseDto<BoardListResponseDto.BoardListDto> BoardListPage() {
        BoardListResponseDto.BoardListDto boardList = boardService.findBoardList();
        return ResponseDto.of(boardList);
    }

    @Operation(summary = "음악 추천 게시판 리스트 하단 페이지",
            description = "상단 팝업을 제외한 하단 페이지 부분, page 파라미터를 통해 다른 페이지 이동가능")
    public ResponseDto<Page<BoardListResponseDto.BoardDto>> getBoards(
            @RequestParam(name = "page", defaultValue = "1") int page) {
        Page<BoardListResponseDto.BoardDto> responseList = boardService.findBoardDto(page);
        return ResponseDto.of(responseList);
    }

    @Operation(summary = "음악 추천 게시판 리스트 하단 페이지 검색",
            description = "상단 팝업을 제외한 하단 페이지 부분, page 파라미터를 통해 다른 페이지 이동가능<br>+" +
                    "searchType 종류는 ['title', 'username ', 'artist ', 'gerne', 'mood'] 종류로 있습니다.<br>" +
                    "keyword는 검색창에 입력한 단어입니다.")
    public ResponseDto<Page<BoardListResponseDto.BoardDto>> getBoardsByKeyword(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "searchType") String searchType,
            @RequestParam(name = "keyword") String keyword) {
        Page<BoardListResponseDto.BoardDto> responseList = boardService.search(page, searchType, keyword);
        return ResponseDto.of(responseList);
    }

    @PutMapping(value ="/updatePost", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto<Board> updatePost(@RequestParam long boardId, @ModelAttribute @Valid UpdateBoardRequestDto updateRequest,
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

    @PostMapping("recommend")
    public ResponseDto<BoardRecommedDto> recommend(@RequestParam("boardId") Long boardId) {
        BoardRecommedDto boardRecommedDto = boardService.toggleLike(boardId);
        return ResponseDto.of(boardRecommedDto);
    }
}
