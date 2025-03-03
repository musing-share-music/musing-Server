package com.example.musing.admin.board.controller;

import com.example.musing.admin.board.service.AdminBoardService;
import com.example.musing.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/musing/admin/board")
public class AdminBoardController {
    private AdminBoardService adminBoardService;

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