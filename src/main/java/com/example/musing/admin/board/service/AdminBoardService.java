package com.example.musing.admin.board.service;

public interface AdminBoardService {
    void updateBoardStateNeedFix(long boardId);
    void updateBoardStatePermit(long boardId);
}
