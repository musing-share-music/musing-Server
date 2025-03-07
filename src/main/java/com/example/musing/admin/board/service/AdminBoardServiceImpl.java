package com.example.musing.admin.board.service;

import com.example.musing.admin.board.dto.AdminBoardResponseDto;
import com.example.musing.admin.board.repository.AdminBoardRepository;
import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.artist.entity.Artist_Music;
import com.example.musing.board.entity.Board;
import com.example.musing.exception.CustomException;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.genre.entity.Genre_Music;
import com.example.musing.mood.dto.MoodDto;
import com.example.musing.mood.entity.Mood_Music;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.musing.board.entity.CheckRegister.NEED_FIX;
import static com.example.musing.board.entity.CheckRegister.PERMIT;
import static com.example.musing.exception.ErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AdminBoardServiceImpl implements AdminBoardService {
    private final AdminBoardRepository boardRepository;

    private static int PAGESIZE = 8;
    private static String NON_CHECK_PERMIT = "registerPermit";
    private static String DELETED_PAGE = "deletedPage";

    @Override
    public Page<AdminBoardResponseDto.BoardListDto> getRegisterPermitSearchPage(int page, String searchType,
                                                                                 String keyword) {
        return search(page, searchType, keyword, NON_CHECK_PERMIT);
    }

    @Override
    public Page<AdminBoardResponseDto.BoardListDto> getDeletedSearchPage(int page, String searchType,
                                                                                String keyword) {
        return search(page, searchType, keyword, DELETED_PAGE);
    }

    @Override
    public Page<AdminBoardResponseDto.BoardListDto> getRegisterPermitPage(int page) {
        return findBoardPage(page, NON_CHECK_PERMIT);
    }

    @Override
    public Page<AdminBoardResponseDto.BoardListDto> getDeletedPage(int page) {
        return findBoardPage(page, DELETED_PAGE);
    }

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

    private Page<AdminBoardResponseDto.BoardListDto> search(int page, String searchType, String keyword, String boardType) {
        if (page < 1) { // 잘못된 접근으로 throw할때 쿼리문 실행을 안하기 위해 나눠서 체크
            throw new CustomException(BAD_REQUEST_BOARD_PAGE);
        }

        Pageable pageable = PageRequest.of(page - 1, PAGESIZE);

        Page<Board> boards = searchBoards(searchType, keyword, boardType, pageable);

        int totalPages = boards.getTotalPages();

        if (page - 1 > totalPages) {
            throw new CustomException(BAD_REQUEST_BOARD_PAGE);
        }

        return boards.map(AdminBoardResponseDto.BoardListDto::toDto);
    }

    private Page<AdminBoardResponseDto.BoardListDto> findBoardPage(int page, String boardType) {
        if (page < 1) { // 잘못된 접근으로 throw할때 쿼리문 실행을 안하기 위해 나눠서 체크
            throw new CustomException(BAD_REQUEST_BOARD_PAGE);
        }

        Pageable pageable = PageRequest.of(page - 1, PAGESIZE);
        Page<Board> boards = findPage(boardType, pageable);

        int totalPages = boards.getTotalPages();
        if (page - 1 > totalPages) {
            throw new CustomException(BAD_REQUEST_BOARD_PAGE);
        }

        return boards.map(AdminBoardResponseDto.BoardListDto::toDto);
    }

    private Page<Board> findPage(String boardType, Pageable pageable) {
        if ("registerPermit".equals(boardType)) {
            return boardRepository.findActiveBoardPage(pageable);
        } else {
            return boardRepository.findDeleteBoardPage(pageable);
        }
    }

    private Page<Board> searchBoards(String searchType, String keyword, String boardType, Pageable pageable) {
        if ("username".equals(searchType)) {
            if ("registerPermit".equals(boardType)) {
                return boardRepository.findDeleteBoardsByUsername(keyword, pageable);
            } else {
                return boardRepository.findActiveBoardsByUsername(keyword, pageable);
            }
        }
        if ("title".equals(searchType)) {
            if ("registerPermit".equals(boardType)) {
                return boardRepository.findDeleteBoardsByTitle(keyword, pageable);
            } else {
                return boardRepository.findActiveBoardsByTitle(keyword, pageable);
            }
        }
        throw new CustomException(NOT_FOUND_KEYWORD);
    }
}

