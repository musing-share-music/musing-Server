package com.example.musing.board.service;

import com.example.musing.artist.entity.Artist;
import com.example.musing.artist.repository.ArtistRepository;
import com.example.musing.board.dto.BoardDto;
import com.example.musing.board.dto.CreateBoardRequest;
import com.example.musing.board.dto.GenreBoardDto;
import com.example.musing.board.dto.PostDto;
import com.example.musing.board.entity.Board;
import com.example.musing.board.repository.BoardRepository;
import com.example.musing.main.dto.MainPageBoardDto;
import com.example.musing.music.entity.Music;
import com.example.musing.music.repository.MusicRepository;
import com.example.musing.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final MusicRepository musicRepository;
    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;

    @Override
    public List<GenreBoardDto> findBy5GenreBoard(String genre) { //장르로 검색한 게시글들을 엔티티에서 Dto로 전환
        Specification<Board> spec = Specification.where(BoardSpecificaion.hasGenre(genre))
                .and(BoardSpecificaion.isActiveCheckFalse());
        List<Board> boards = boardRepository.findAll(spec, //해당 장르의 게시글 5개를 최신순으로 가져옴
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent();
        return boards.stream().map(this::entityToGenreDto).collect(Collectors.toList());
    }

    @Override
    public BoardDto findHotMusicBoard() {
        //한달이내 생성이 되었고, 추천수가 제일 많으며, 삭제 처리가 되지않음을 확인
        Specification<Board> spec = Specification.where(BoardSpecificaion.isCreateAtAfter())
                .and(BoardSpecificaion.isActiveCheckFalse()).and(BoardSpecificaion.orderByRecommendCountDesc());
        Optional<Board> dto = boardRepository.findOne(spec);
        //레파지토리에 상속받은 JpaSpecificationExecutor로 사용
        return dto.map(this::entityToBoardDto).orElse(null); //dto로 변환할 Board가 없으면 null반환
    }

    @Override
    public List<MainPageBoardDto> findBy5Board() {
        //삭제 처리가 되지않은 게시글
        Specification<Board> spec = Specification.where(BoardSpecificaion.isActiveCheckFalse());
        List<Board> boards = boardRepository.findAll(spec, //해당 장르의 게시글 5개를 최신순으로 가져옴
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent();
        return boards.stream().map(this::entityToMainDto).collect(Collectors.toList());
    }

    public List<BoardDto> findBy10LikeMusics(String userId) {
        Specification<Board> spec = Specification.where(BoardSpecificaion.isActiveCheckFalse());
        List<Board> boards = boardRepository.findByUser_Id(spec, //해당 장르의 게시글 5개를 최신순으로 가져옴
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id")), userId).getContent();
        return boards.stream().map(this::entityToBoardDto).collect(Collectors.toList());
    }

    //게시판 등록 로직
    @Override
    public PostDto createBoard(CreateBoardRequest request) {
        // Artist 조회
        Artist artistEntity = artistRepository.findByName(request.getArtist())
                .orElseThrow(() -> new EntityNotFoundException("Artist not found: " + request.getArtist()));

        // Music 조회 또는 생성


        // Board 생성
        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(userRepository.findByEmail(request.getUserEmail())
                        .orElseThrow(() -> new EntityNotFoundException("User not found: " + request.getUserEmail())))
                .build();

        Board savedBoard = boardRepository.save(board);
        return PostDto.fromEntity(savedBoard);

        // 4. DTO로 변환 후 반환

    }

    private GenreBoardDto entityToGenreDto(Board board) { //장르로 검색한 게시글 엔티티를 Dto로 전환
        return GenreBoardDto.toDto(board);
    }

    private BoardDto entityToBoardDto(Board board) { //핫한 게시글을 엔티티에서 Dto로 전환
        return BoardDto.toDto(board);
    }

    private MainPageBoardDto entityToMainDto(Board board) { //게시글을 엔티티에서 Dto로 전환
        return MainPageBoardDto.toDto(board);
    }


}


