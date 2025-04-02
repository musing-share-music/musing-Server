package com.example.musing.admin.board.service;

import com.example.musing.admin.board.dto.AdminBoardResponseDto;
import com.example.musing.board.dto.DetailResponse;
import com.example.musing.board.entity.Board;
import org.springframework.data.domain.Page;

public interface AdminBoardService {

    DetailResponse selectDetail(long boardId);
    Page<AdminBoardResponseDto.AdminBoardListDto> getRegisterPermitSearchPage(int page, String searchType, String keyword);
    Page<AdminBoardResponseDto.AdminBoardListDto> getDeletedSearchPage(int page, String searchType, String keyword);
    Page<AdminBoardResponseDto.AdminBoardListDto> getRegisterPermitPage(int page);
    Page<AdminBoardResponseDto.AdminBoardListDto> getDeletedPage(int page);

    void updateBoardStateNeedFix(long boardId);
    void updateBoardStatePermit(long boardId);
}
