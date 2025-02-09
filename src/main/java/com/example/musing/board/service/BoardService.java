package com.example.musing.board.service;

import com.example.musing.board.dto.*;
import com.example.musing.main.dto.RecommendBoardRight;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardService {
    List<GenreBoardDto> findBy5GenreBoard(String genre);

    RecommendBoardLeft findHotMusicBoard();

    List<RecommendBoardRight> findBy5Board();

    List<GenreBoardDto> findBy10LikeMusics(String userId);

    //게시판 등록 로직
    void createBoard(CreateBoardRequest request, List<MultipartFile> images);





    BoardListRequestDto.BoardListDto findBoardList();
    Page<BoardListRequestDto.BoardDto> findBoardDto(int page);

    Page<BoardListRequestDto.BoardDto> search(int page, String searchType, String keyword);

    BoardAndReplyPageDto findBoardDetailPage(long boardId);

    // 글 삭제
    void deleteBoard(Long boardId);
    //글 수정
     void updateBoard(UpdateBoardRequestDto request, List<MultipartFile> images);

     DetailResponse selectDetail(long boardId);
}
