package com.example.musing.admin.board.service;

import com.example.musing.admin.board.dto.AdminBoardResponseDto;
import com.example.musing.admin.board.repository.AdminBoardRepository;
import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.artist.entity.Artist_Music;
import com.example.musing.board.dto.BoardListResponseDto;
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

    @Override
    public Page<AdminBoardResponseDto.BoardDto> search(int page, String searchType, String keyword) {
        if (page < 1) { // 잘못된 접근으로 throw할때 쿼리문 실행을 안하기 위해 나눠서 체크
            throw new CustomException(BAD_REQUEST_BOARD_PAGE);
        }

        Pageable pageable = PageRequest.of(page - 1, PAGESIZE);

        Page<Board> boards = searchBoards(searchType, keyword, pageable);

        int totalPages = boards.getTotalPages();

        if (page - 1 > totalPages) {
            throw new CustomException(BAD_REQUEST_BOARD_PAGE);
        }

        return boards.map(board -> {
            List<GenreDto> genreList = getGenreMusicListDto(board);
            List<MoodDto> moodList = getMoodMusicListDto(board);
            List<ArtistDto> artistList = getArtistMusicListDto(board);
            return AdminBoardResponseDto.BoardDto.toDto(board, genreList, moodList, artistList);
        });
    }

    @Override
    public Page<AdminBoardResponseDto.BoardDto> findBoardDto(int page) {
        if (page < 1) { // 잘못된 접근으로 throw할때 쿼리문 실행을 안하기 위해 나눠서 체크
            throw new CustomException(BAD_REQUEST_BOARD_PAGE);
        }

        Pageable pageable = PageRequest.of(page - 1, PAGESIZE);
        Page<Board> boards = boardRepository.findActiveBoardPage(pageable);

        int totalPages = boards.getTotalPages();
        if (page - 1 > totalPages) {
            throw new CustomException(BAD_REQUEST_BOARD_PAGE);
        }

        return boards.map(board -> {
            List<GenreDto> genreList = getGenreMusicListDto(board);
            List<MoodDto> moodList = getMoodMusicListDto(board);
            List<ArtistDto> artistList = getArtistMusicListDto(board);
            return AdminBoardResponseDto.BoardDto.toDto(board, genreList, moodList, artistList);
        });
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

    private Page<Board> searchBoards(String searchType, String keyword, Pageable pageable) {
        return switch (searchType) {
            case "username" -> boardRepository.findActiveBoardsByUsername(keyword, pageable);
            case "title" -> boardRepository.findActiveBoardsByTitle(keyword, pageable);
            default -> throw new CustomException(NOT_FOUND_KEYWORD);
        };
    }

    //게시글의 음악에 포함된 장르를 Dto로 담아 리스트로 반환
    private List<GenreDto> getGenreMusicListDto(Board board) {
        return board.getMusic().getGenreMusics().stream()
                .map(Genre_Music::getGenre)
                .map(GenreDto::toDto)
                .collect(Collectors.toList());
    }

    //게시글의 음악에 포함된 분위기를 Dto로 담아 리스트로 반환
    private List<MoodDto> getMoodMusicListDto(Board board) {
        return board.getMusic().getMoodMusics().stream()
                .map(Mood_Music::getMood)
                .map(MoodDto::toDto)
                .collect(Collectors.toList());
    }

    //게시글의 음악에 포함된 아티스트를 Dto로 담아 리스트로 반환
    private List<ArtistDto> getArtistMusicListDto(Board board) {
        return board.getMusic().getArtists().stream()
                .map(Artist_Music::getArtist)
                .map(ArtistDto::toDto)
                .collect(Collectors.toList());
    }
}
