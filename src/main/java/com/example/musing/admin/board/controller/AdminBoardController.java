package com.example.musing.admin.board.controller;

import com.example.musing.admin.board.dto.AdminBoardResponseDto;
import com.example.musing.admin.board.service.AdminBoardService;
import com.example.musing.board.dto.BoardListResponseDto;
import com.example.musing.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/musing/admin/board")
public class AdminBoardController {
    private AdminBoardService adminBoardService;

    @GetMapping("/list/page")
    public ResponseDto<Page<AdminBoardResponseDto.BoardDto>> getBoards(
            @RequestParam(name = "page", defaultValue = "1") int page) {
        Page<AdminBoardResponseDto.BoardDto> responseList = adminBoardService.findBoardDto(page);
        return ResponseDto.of(responseList);
    }

    @GetMapping("/list/page/search")
    public ResponseDto<Page<AdminBoardResponseDto.BoardDto>> getBoardsByKeyword(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "searchType") String searchType,
            @RequestParam(name = "keyword") String keyword) {
        Page<AdminBoardResponseDto.BoardDto> responseList = adminBoardService.search(page, searchType, keyword);
        return ResponseDto.of(responseList);
    }

    @PutMapping("/non/permit")
    public ResponseDto<String> updateBoardStateNeedFix(@RequestParam long boardId) {
        adminBoardService.updateBoardStateNeedFix(boardId);
        return ResponseDto.of("", "관리자 확인 결과 수정이 필요합니다.");
    }

    @PutMapping("permit")
    public ResponseDto<String> updateBoardStatePermit(@RequestParam long boardId) {
        adminBoardService.updateBoardStatePermit(boardId);
        return ResponseDto.of("", "관리자 확인 결과 승인되었습니다.");
    }
}