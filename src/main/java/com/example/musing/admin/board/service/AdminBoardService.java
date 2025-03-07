package com.example.musing.admin.board.service;

import com.example.musing.admin.board.dto.AdminBoardResponseDto;
import org.springframework.data.domain.Page;

public interface AdminBoardService {
    Page<AdminBoardResponseDto.BoardDto> search(int page, String searchType, String keyword);
    Page<AdminBoardResponseDto.BoardDto> findBoardDto(int page);
    void updateBoardStateNeedFix(long boardId);
    void updateBoardStatePermit(long boardId);
}
