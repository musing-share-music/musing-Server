package com.example.musing.board.service;

import com.example.musing.board.dto.BoardDto;
import com.example.musing.board.entity.Board;
import com.example.musing.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{
    private final BoardRepository boardRepository;
    @Override
    public BoardDto.GenreBoardDto entityToDto_Genre(Board board) { //장르로 검색한 게시글 엔티티를 Dto로 전환
        return BoardDto.GenreBoardDto.builder()
                .id(board.getId())
                .musicName(board.getMusic_id().getName())
                .thumbNailLink(board.getMusic_id().getThumbNailLink())
                .build();
    }
    @Override
    @Transactional
    public List<BoardDto.GenreBoardDto> entitysToDtos(String genre) { //장르로 검색한 게시글들을 엔티티에서 Dto로 전환
        List<Board> boards = findByTop5BoardGenre(genre);
        return boards.stream().map(this::entityToDto_Genre).collect(Collectors.toList());
    }
    @Override
    @Transactional
    public BoardDto.HotMusicBoardDto entityToDto_HotBoard(Board board) { //핫한 게시글을 엔티티에서 Dto로 전환
        return BoardDto.HotMusicBoardDto.builder()
                .title(board.getTitle())
                .musicName(board.getMusic_id().getName())
                .artist(board.getMusic_id().getArtist())
                .thumbNailLink(board.getMusic_id().getThumbNailLink())
                .build();
    }
    @Override
    public BoardDto.MainPageBoardDto entityToDto(Board board) { //게시글을 엔티티에서 Dto로 전환
        return BoardDto.MainPageBoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .username(board.getUser_id().getUsername())
                .replyCount(board.getReplies().size()) //해당부분은 조회 최적화를 위해 Count만 가져올 예정
                .recommendCount(board.getRecommendCount())
                .viewCount(board.getViewCount())
                .musicName(board.getMusic_id().getName())
                .artist(board.getMusic_id().getArtist())
                .thumbNailLink(board.getMusic_id().getThumbNailLink())
                .build();
    }

    @Override
    @Transactional
    public List<BoardDto.MainPageBoardDto> entitysToDtos() { //최신 게시글들을 Dto로 전환
        List<Board> boards = findByTop5Board();
        return boards.stream().map(this::entityToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Board> findByTop5BoardGenre(String genre) { //해당장르의 게시글을 최대 5개 가져옴
        Specification<Board> spec = Specification.where(BoardSpecificaion.hasGenre(genre))
                .and(BoardSpecificaion.isActiveCheckFalse());
        return boardRepository.findAll(spec, //해당 장르의 게시글 5개를 최신순으로 가져옴
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Board> findHotMusicBoard() { 
        //한달이내 생성이 되었고, 추천수가 제일 많으며, 삭제 처리가 되지않음을 확인
        Specification<Board> spec = Specification.where(BoardSpecificaion.isCreateAtAfter())
                .and(BoardSpecificaion.isActiveCheckFalse()).and(BoardSpecificaion.orderByRecommendCountDesc());
        return boardRepository.findOne(spec); //레파지토리에 상속받은 JpaSpecificationExecutor로 사용
    }

    @Override
    @Transactional(readOnly = true)
    public List<Board> findByTop5Board() {
        //삭제 처리가 되지않은 게시글
        Specification<Board> spec = Specification.where(BoardSpecificaion.isActiveCheckFalse());
        return  boardRepository.findAll(spec, //해당 장르의 게시글 5개를 최신순으로 가져옴
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent();
    }


}
