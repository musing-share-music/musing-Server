package com.example.musing.board.service;

import com.example.musing.board.dto.BoardDto;
import com.example.musing.board.dto.GenreBoardDto;
import com.example.musing.board.entity.Board;
import com.example.musing.board.repository.BoardRepository;
import com.example.musing.like_music.entity.Like_Music;
import com.example.musing.like_music.playlist.LikeMusicRepository;
import com.example.musing.like_music.repository.Like_MusicRepository;
import com.example.musing.main.dto.MainPageBoardDto;
import com.example.musing.music.entity.Music;
import com.example.musing.user.entity.User;
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
    private final Like_MusicRepository likeMusicRepository;
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
        List<Board> boards = boardRepository.findAll(spec, PageRequest.of(0, 1)).getContent();

        //레파지토리에 상속받은 JpaSpecificationExecutor로 사용
        return boards.stream().map(this::entityToBoardDto).findFirst().orElse(null);
    }

    @Override
    public List<MainPageBoardDto> findBy5Board() {
        //삭제 처리가 되지않은 게시글
        Specification<Board> spec = Specification.where(BoardSpecificaion.isActiveCheckFalse());
        List<Board> boards = boardRepository.findAll(spec, //해당 장르의 게시글 5개를 최신순으로 가져옴
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent();
        return boards.stream().map(this::entityToMainDto).collect(Collectors.toList());
    }

    public List<GenreBoardDto> findBy10LikeMusics(String userId) {
        // 사용자가 좋아요를 누른 음악 목록을 ID가 높은 순서로 10개만 가져옵니다.
        List<Like_Music> likedMusicList = likeMusicRepository.findTop10ByUserOrderByIdDesc(userId);

        // 음악 목록을 추출합니다.
        List<Music> musicList = likedMusicList.stream()
                .map(Like_Music::getMusic)
                .collect(Collectors.toList());

        // fetch join을 사용하여 해당 음악에 관련된 게시글을 가져옵니다.
        List<Board> boards = boardRepository.findBoardsByMusicList(musicList);
        return boards.stream().map(this::entityToGenreDto).collect(Collectors.toList());
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
