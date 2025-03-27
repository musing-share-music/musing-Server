package com.example.musing.admin.board.controller;

import com.example.musing.admin.board.dto.AdminBoardResponseDto;
import com.example.musing.admin.board.service.AdminBoardService;
import com.example.musing.alarm.service.AlarmService;
import com.example.musing.board.dto.DetailResponse;
import com.example.musing.board.entity.Board;
import com.example.musing.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import static com.example.musing.alarm.entity.AlarmType.APPLYPERMIT;

@RequiredArgsConstructor
@RestController
@RequestMapping("/musing/admin/board")
public class AdminBoardController {
    private final AdminBoardService adminBoardService;
    private final AlarmService alarmService;

    private static final String ADMIN_DENY_ALARM_CONTENT = "작성한 게시글의 승인이 거절되었습니다.";
    private static final String ADMIN_PERMIT_ALARM_CONTENT = "작성한 게시글이 승인되었어요.";
    private static final String ALARM_API_URL = "/musing/board/selectDetail?boardId=";

    @GetMapping("/removed")
    public ResponseDto<DetailResponse> getDeletedBoardDetail(@RequestParam Long boardId) {
        DetailResponse responseDto = adminBoardService.selectDetail(boardId);
        return ResponseDto.of(responseDto);
    }
    @GetMapping("/list/removed")
    public ResponseDto<Page<AdminBoardResponseDto.AdminBoardListDto>> getDeletedBoards(
            @RequestParam(name = "page", defaultValue = "1") int page) {
        Page<AdminBoardResponseDto.AdminBoardListDto> responseList = adminBoardService.getDeletedPage(page);
        return ResponseDto.of(responseList);
    }

    @GetMapping("/list/removed/search")
    public ResponseDto<Page<AdminBoardResponseDto.AdminBoardListDto>> getDeletedBoardsByKeyword(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "searchType") String searchType,
            @RequestParam(name = "keyword") String keyword) {
        Page<AdminBoardResponseDto.AdminBoardListDto> responseList =
                adminBoardService.getDeletedSearchPage(page, searchType, keyword);
        return ResponseDto.of(responseList);
    }

    @GetMapping("/list")
    public ResponseDto<Page<AdminBoardResponseDto.AdminBoardListDto>> getBoards(
            @RequestParam(name = "page", defaultValue = "1") int page) {
        Page<AdminBoardResponseDto.AdminBoardListDto> responseList = adminBoardService.getRegisterPermitPage(page);
        return ResponseDto.of(responseList);
    }

    @GetMapping("/list/search")
    public ResponseDto<Page<AdminBoardResponseDto.AdminBoardListDto>> getBoardsByKeyword(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "searchType") String searchType,
            @RequestParam(name = "keyword") String keyword) {
        Page<AdminBoardResponseDto.AdminBoardListDto> responseList =
                adminBoardService.getRegisterPermitSearchPage(page, searchType, keyword);
        return ResponseDto.of(responseList);
    }

    @PutMapping("/non/permit")
    public ResponseDto<String> updateBoardStateNeedFix(@RequestParam long boardId) {
        adminBoardService.updateBoardStateNeedFix(boardId);

        Board board = adminBoardService.findByBoardId(boardId);
        String boardUrl = ALARM_API_URL + board.getId();

        alarmService.send(board.getUser(), APPLYPERMIT, ADMIN_DENY_ALARM_CONTENT, boardUrl);

        return ResponseDto.of("", "관리자 확인 결과 수정이 필요합니다.");
    }

    @PutMapping("permit")
    public ResponseDto<String> updateBoardStatePermit(@RequestParam long boardId) {
        adminBoardService.updateBoardStatePermit(boardId);

        Board board = adminBoardService.findByBoardId(boardId);
        String boardUrl = ALARM_API_URL + board.getId();

        alarmService.send(board.getUser(), APPLYPERMIT, ADMIN_PERMIT_ALARM_CONTENT, boardUrl);
        return ResponseDto.of("", "관리자 확인 결과 승인되었습니다.");
    }
}