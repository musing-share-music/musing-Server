package com.example.musing.admin.board.service;

import com.example.musing.admin.board.repository.AdminBoardRepository;
import com.example.musing.board.entity.Board;
import com.example.musing.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.musing.board.entity.CheckRegister.NEED_FIX;
import static com.example.musing.board.entity.CheckRegister.PERMIT;
import static com.example.musing.exception.ErrorCode.NOT_FOUND_BOARD;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AdminBoardServiceImpl implements AdminBoardService {
    private final AdminBoardRepository boardRepository;



    @Transactional
    @Override
    public void updateBoardStateNeedFix(long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new CustomException(NOT_FOUND_BOARD));
        board.updateRegister(NEED_FIX);
    }

    @Transactional
    @Override
    public void updateBoardStatePermit(long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new CustomException(NOT_FOUND_BOARD));
        board.updateRegister(PERMIT);
    }
}
