package com.example.musing.board.service;

import com.example.musing.artist.entity.Artist;
import com.example.musing.artist.repository.ArtistRepository;
import com.example.musing.board.dto.*;
import com.example.musing.board.entity.Board;
import com.example.musing.board.repository.BoardRepository;
import com.example.musing.hashtag.entity.HashTag;
import com.example.musing.like_music.entity.Like_Music;
import com.example.musing.like_music.repository.Like_MusicRepository;
import com.example.musing.main.dto.MainPageBoardDto;
import com.example.musing.music.entity.Music;
import com.example.musing.music.repository.MusicRepository;
import com.example.musing.user.entity.User;
import com.example.musing.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.Random;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ArtistRepository artistRepository;
    private final Like_MusicRepository likeMusicRepository;
    private final MusicRepository musicRepository;

    @Override
    public List<GenreBoardDto> findBy5GenreBoard(String genre) { //장르로 검색한 게시글들을 엔티티에서 Dto로 전환
        Specification<Board> spec = Specification.where(BoardSpecificaion.hasGenre(genre))
                .and(BoardSpecificaion.isActiveCheckFalse());
        List<Board> boards = boardRepository.findAll(spec, //해당 장르의 게시글 5개를 최신순으로 가져옴
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent();
        return boards.stream().map(this::entityToGenreDto).collect(Collectors.toList());
    }

    @Override
    public HotBoardDto findHotMusicBoard() {
        //한달이내 생성이 되었고, 추천수가 제일 많으며, 삭제 처리가 되지않음을 확인
        Specification<Board> spec = Specification.where(BoardSpecificaion.isCreateAtAfterMonth())
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

    //게시판 등록 로직
    @Transactional
    @Override
    public void createBoard(CreateBoardRequest request) {

        String fileName = UUID.randomUUID() + "_" + request.getImage().getOriginalFilename();

        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .image(fileName)
                .activeCheck(false)
                .recommendCount(0)
                .viewCount(0)
                .build();
        boardRepository.save(board);
    }

    @Override
    public List<CreateBoardResponse> getAllBoards() {

        List<Board> boards = boardRepository.findAll(); // 모든 게시글을 조회

        // 각 Board에 대해 CreateBoardResponse 객체 생성 후 리스트로 반환
        return boards.stream()
                .map(board -> {
                    // Music, Artist, Hashtags 정보를 가져옴
                    Music music = board.getMusic();
                    Artist artist = board.getMusic().getArtist();
                    List<HashTag> hashtags = board.getMusic().getHashTagList();
                    User user = board.getUser();

                    CreateBoardResponse response = new CreateBoardResponse();

                    response.setUserEmail(user.getEmail());
                    response.setTitle(board.getTitle());
                    response.setMusicTitle(music.getName()); // Music 엔티티에서 제목을 가져옴
                    response.setArtist(artist.getName());  // Artist 엔티티에서 이름을 가져옴
                    response.setYoutubeLink(music.getSongLink()); // 유튜브 링크
                    response.setHashtags(hashtags.stream()
                            .map(HashTag::getHashtag)
                            .collect(Collectors.toList())); // 해시태그 목록
                    response.setGenre(music.getGenre());  // 음악의 장르 가져오기
                    response.setImageUrl(board.getImage()); // 이미지 URL 처리
                    response.setContent(board.getContent());


                    response.setCreatedAt(board.getCreatedAt());
                    response.setUpdatedAt(board.getUpdatedAt());

                    // 생성된 response 객체 반환
                    return response;
                })
                .collect(Collectors.toList()); // List<CreateBoardResponse>로 반환
    }

    @Override
    public void deleteBoard(Long boardId) {

        if(!boardRepository.existsById(boardId)) {
            throw new EntityNotFoundException("Board does not exist");
        }
       boardRepository.deleteById(boardId);
    }

    @Override
    public void updateBoard(Long boardId, UpdateBoardRequestDto updateRequest) {


        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID: " + boardId));



        String fileName = UUID.randomUUID() + "_" + updateRequest.getImage().getOriginalFilename();

        // 2. 업데이트 요청에 따라 필드 수정
        if (updateRequest.getTitle() != null) {
            board.builder().title(updateRequest.getTitle()).build();
        }

        if(updateRequest.getMusicTitle() != null) {
            board.getMusic().builder().name(updateRequest.getMusicTitle()).build();
        }

        if(updateRequest.getArtist() != null) {
            board.getMusic().getArtist().builder().name(updateRequest.getArtist()).build();
        }
        if(updateRequest.getYoutubeLink() != null) {
            board.getMusic().builder().songLink(updateRequest.getYoutubeLink()).build();
        }
        if (updateRequest.getContent() != null) {
            board.builder().content(updateRequest.getContent());
        }

        if (updateRequest.getHashtags() != null && !updateRequest.getHashtags().isEmpty()) {
            // 기존 해시태그를 제거
            board.getMusic().getHashTagList().clear();

            // 새로운 해시태그 추가
            updateRequest.getHashtags().forEach(tag -> {
                // 각 해시태그 엔티티 생성 및 관계 설정
                HashTag newHashTag = HashTag.builder()
                        .hashtag(tag)
                        .music(board.getMusic()) // Music 객체 설정
                        .build();

                // Music 객체에 해시태그 추가
                board.getMusic().addHashTag(newHashTag);
            });
        }
        if (updateRequest.getImage().getOriginalFilename() != null) {
            board.builder().image(fileName);
        }

        if(updateRequest.getGenre() != null){
            board.getMusic().builder().genre(updateRequest.getGenre());
        }
        // 3. 수정된 엔티티 저장
        boardRepository.save(board);

    }

    private GenreBoardDto entityToGenreDto(Board board) { //장르로 검색한 게시글 엔티티를 Dto로 전환
        return GenreBoardDto.toDto(board);
    }

    private HotBoardDto entityToBoardDto(Board board) { //핫한 게시글을 엔티티에서 Dto로 전환
        return HotBoardDto.toDto(board);
    }

    private MainPageBoardDto entityToMainDto(Board board) { //게시글을 엔티티에서 Dto로 전환
        return MainPageBoardDto.toDto(board);
    }
}


