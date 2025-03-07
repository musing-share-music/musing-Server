package com.example.musing.admin.board.service;

import com.example.musing.admin.board.dto.AdminBoardResponseDto;
import org.springframework.data.domain.Page;

public interface AdminBoardService {
    Page<AdminBoardResponseDto.BoardListDto> getRegisterPermitSearchPage(int page, String searchType,
                                                                         String keyword);
    Page<AdminBoardResponseDto.BoardListDto> getDeletedSearchPage(int page, String searchType,
                                                                  String keyword);
    Page<AdminBoardResponseDto.BoardListDto> getRegisterPermitPage(int page);
    Page<AdminBoardResponseDto.BoardListDto> getDeletedPage(int page);

    void updateBoardStateNeedFix(long boardId);
    void updateBoardStatePermit(long boardId);
}
