package com.example.musing.admin.board.controller;

import com.example.musing.admin.board.dto.AdminBoardResponseDto;
import com.example.musing.admin.board.service.AdminBoardService;
import com.example.musing.common.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/musing/admin/board")
public class AdminBoardController {
    private AdminBoardService adminBoardService;

    @Operation(summary = "삭제된 게시글 조회",
            description = "page 파라미터를 통해 다른 페이지 이동이 가능합니다.")
    @GetMapping("/list/removed/page")
    public ResponseDto<Page<AdminBoardResponseDto.BoardListDto>> getDeleteBoards(
            @RequestParam(name = "page", defaultValue = "1") int page) {
        Page<AdminBoardResponseDto.BoardListDto> responseList = adminBoardService.getDeletedPage(page);
        return ResponseDto.of(responseList);
    }

    @Operation(summary = "삭제된 게시글 조회 검색",
            description = "page 파라미터를 통해 다른 페이지 이동이 가능합니다.<br>" +
                    "searchType 종류는 ['title', 'username'] 종류로 있습니다.<br>" +
                    "keyword는 검색창에 입력한 단어입니다.")
    @GetMapping("/list/removed/page/search")
    public ResponseDto<Page<AdminBoardResponseDto.BoardListDto>> getDeleteBoardsByKeyword(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "searchType") String searchType,
            @RequestParam(name = "keyword") String keyword) {
        Page<AdminBoardResponseDto.BoardListDto> responseList =
                adminBoardService.getDeletedSearchPage(page, searchType, keyword);
        return ResponseDto.of(responseList);
    }
    @Operation(summary = "승인 요청이 된 페이지 리스트 조회",
            description = "page 파라미터를 통해 다른 페이지 이동이 가능합니다.")
    @GetMapping("/list/page")
    public ResponseDto<Page<AdminBoardResponseDto.BoardListDto>> getBoards(
            @RequestParam(name = "page", defaultValue = "1") int page) {
        Page<AdminBoardResponseDto.BoardListDto> responseList = adminBoardService.getRegisterPermitPage(page);
        return ResponseDto.of(responseList);
    }

    @Operation(summary = "승인 요청이 된 페이지 리스트 조회 검색",
            description = "page 파라미터를 통해 다른 페이지 이동이 가능합니다.<br>" +
                    "searchType 종류는 ['title', 'username'] 종류로 있습니다.<br>" +
                    "keyword는 검색창에 입력한 단어입니다.")
    @GetMapping("/list/page/search")
    public ResponseDto<Page<AdminBoardResponseDto.BoardListDto>> getBoardsByKeyword(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "searchType") String searchType,
            @RequestParam(name = "keyword") String keyword) {
        Page<AdminBoardResponseDto.BoardListDto> responseList =
                adminBoardService.getRegisterPermitSearchPage(page, searchType, keyword);
        return ResponseDto.of(responseList);
    }

    @Operation(summary = "관리자 승인을 거절합니다.")
    @PutMapping("/non/permit")
    public ResponseDto<String> updateBoardStateNeedFix(@RequestParam long boardId) {
        adminBoardService.updateBoardStateNeedFix(boardId);
        return ResponseDto.of("", "관리자 확인 결과 수정이 필요합니다.");
    }

    @Operation(summary = "관리자 승인을 허용합니다.")
    @PutMapping("permit")
    public ResponseDto<String> updateBoardStatePermit(@RequestParam long boardId) {
        adminBoardService.updateBoardStatePermit(boardId);
        return ResponseDto.of("", "관리자 확인 결과 승인되었습니다.");
    }
}