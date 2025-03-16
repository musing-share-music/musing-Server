package com.example.musing.admin.board.service;

import com.example.musing.admin.board.dto.AdminBoardResponseDto;
import com.example.musing.admin.board.repository.AdminBoardRepository;
import com.example.musing.board.dto.DetailResponse;
import com.example.musing.board.entity.Board;
import com.example.musing.exception.CustomException;
import com.example.musing.genre.entity.Genre_Music;
import com.example.musing.music.entity.Music;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
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
    public DetailResponse selectDetail(long boardId) {
        Board board = boardRepository.findBoardByActiveCheckFalse(boardId);
        if (!boardRepository.existsById(boardId)) {
            throw new CustomException(NOT_FOUND_BOARD);
        }

        Music music = board.getMusic();

        // 첫 번째 Music의 Artist 정보 가져오기
        List<String> artistNames = music.getArtists().stream()
                .map(am -> am.getArtist().getName())
                .toList();

        List<String> genreNames = music.getGenreMusics().stream()
                .map(Genre_Music::getGenre)
                .map(genre -> genre.getGenreName().getKey()).toList();

        return DetailResponse.of(board, artistNames, extractHashtags(board.getContent()), genreNames);
    }
    @Override
    public Page<AdminBoardResponseDto.AdminBoardListDto> getRegisterPermitSearchPage(int page, String searchType,
                                                                                     String keyword) {
        return search(page, searchType, keyword, NON_CHECK_PERMIT);
    }

    @Override
    public Page<AdminBoardResponseDto.AdminBoardListDto> getDeletedSearchPage(int page, String searchType,
                                                                              String keyword) {
        return search(page, searchType, keyword, DELETED_PAGE);
    }

    @Override
    public Page<AdminBoardResponseDto.AdminBoardListDto> getRegisterPermitPage(int page) {
        return findBoardPage(page, NON_CHECK_PERMIT);
    }

    @Override
    public Page<AdminBoardResponseDto.AdminBoardListDto> getDeletedPage(int page) {
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

    private List<String> extractHashtags(String content) {
        if (content == null) return Collections.emptyList();
        return Arrays.stream(content.split("\\s+"))
                .filter(word -> word.startsWith("#"))
                .collect(Collectors.toList());
    }

    private Page<AdminBoardResponseDto.AdminBoardListDto> search(int page, String searchType, String keyword, String boardType) {
        if (page < 1) { // 잘못된 접근으로 throw할때 쿼리문 실행을 안하기 위해 나눠서 체크
            throw new CustomException(BAD_REQUEST_BOARD_PAGE);
        }

        Pageable pageable = PageRequest.of(page - 1, PAGESIZE);

        Page<Board> boards = searchBoards(searchType, keyword, boardType, pageable);

        int totalPages = boards.getTotalPages();

        if (page - 1 > totalPages) {
            throw new CustomException(BAD_REQUEST_BOARD_PAGE);
        }

        return boards.map(AdminBoardResponseDto.AdminBoardListDto::toDto);
    }

    private Page<AdminBoardResponseDto.AdminBoardListDto> findBoardPage(int page, String boardType) {
        if (page < 1) { // 잘못된 접근으로 throw할때 쿼리문 실행을 안하기 위해 나눠서 체크
            throw new CustomException(BAD_REQUEST_BOARD_PAGE);
        }

        Pageable pageable = PageRequest.of(page - 1, PAGESIZE);
        Page<Board> boards = findPage(boardType, pageable);

        int totalPages = boards.getTotalPages();
        if (page - 1 > totalPages) {
            throw new CustomException(BAD_REQUEST_BOARD_PAGE);
        }

        return boards.map(AdminBoardResponseDto.AdminBoardListDto::toDto);
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

