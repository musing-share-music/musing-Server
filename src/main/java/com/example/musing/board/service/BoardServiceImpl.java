package com.example.musing.board.service;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.artist.entity.Artist_Music;
import com.example.musing.artist.repository.ArtistRepository;
import com.example.musing.artist.repository.Artist_MusicRepository;
import com.example.musing.board.dto.*;
import com.example.musing.board.entity.Board;
import com.example.musing.board.repository.BoardRepository;
import com.example.musing.exception.CustomException;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.genre.entity.Genre_Music;
import com.example.musing.genre.repository.Genre_MusicRepository;
import com.example.musing.hashtag.entity.HashTag;
import com.example.musing.like_music.entity.Like_Music;
import com.example.musing.like_music.repository.Like_MusicRepository;
import com.example.musing.main.dto.RecommendBoardRight;
import com.example.musing.mood.dto.MoodDto;
import com.example.musing.mood.entity.Mood_Music;
import com.example.musing.mood.repository.Mood_MusicRepository;
import com.example.musing.music.entity.Music;
import com.example.musing.music.repository.MusicRepository;
import com.example.musing.reply.dto.ReplyDto;
import com.example.musing.reply.service.ReplyService;
import com.example.musing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.musing.exception.ErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

    private static PageRequest pageRequestOrderBy = PageRequest.
            of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
    private static PageRequest pageRequest = PageRequest.of(0, 1);
    private static int PAGESIZE = 8;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ArtistRepository artistRepository;
    private final Like_MusicRepository likeMusicRepository;
    private final MusicRepository musicRepository;
    private final ReplyService replyService;

    private final Artist_MusicRepository artist_musicRepository;
    private final Genre_MusicRepository genre_musicRepository;
    private final Mood_MusicRepository mood_musicRepository;

    @Override
    public List<GenreBoardDto> findBy5GenreBoard(String genre) { //장르로 검색한 게시글들을 엔티티에서 Dto로 전환
        Specification<Board> spec = Specification.where(BoardSpecificaion.hasGenre(genre))
                .and(BoardSpecificaion.isActiveCheckTrue());

        List<Board> boards = findBySpecBoard(spec, pageRequestOrderBy);

        return boards.stream().map(board -> entityToGenreDto(board, getArtistMusicListDto(board)))
                .collect(Collectors.toList());
    }

    @Override
    public RecommendBoardLeft findHotMusicBoard() {
        //한달이내 생성이 되었고, 추천수가 제일 많으며, 삭제 처리가 되지않음을 확인
        Specification<Board> spec = Specification.where(BoardSpecificaion.isCreateAtAfterMonth())
                .and(BoardSpecificaion.isActiveCheckTrue()).and(BoardSpecificaion.orderByRecommendCountDesc());

        List<Board> boards = findBySpecBoard(spec, pageRequest);

        //레파지토리에 상속받은 JpaSpecificationExecutor로 사용
        return boards.stream().map(this::entityToBoardDto).findFirst().orElse(null);
    }

    @Override
    public List<RecommendBoardRight> findBy5Board() {
        //삭제 처리가 되지않은 게시글
        Specification<Board> spec = Specification.where(BoardSpecificaion.isActiveCheckTrue());

        List<Board> boards = findBySpecBoard(spec, pageRequestOrderBy); //해당 장르의 게시글 5개를 최신순으로 가져옴

        return boards.stream().map(this::entityToMainDto).collect(Collectors.toList());
    }

    @Override
    public List<GenreBoardDto> findBy10LikeMusics(String userId) {
        // 사용자가 좋아요를 누른 음악 목록을 ID가 높은 순서로 10개만 가져옵니다.
        List<Like_Music> likedMusicList = likeMusicRepository.findTop10ByUserOrderByIdDesc(userId);

        // 음악 목록을 추출합니다.
        List<Music> musicList = likedMusicList.stream()
                .map(Like_Music::getMusic)
                .collect(Collectors.toList());

        // fetch join을 사용하여 해당 음악에 관련된 게시글을 가져옵니다.
        List<Board> boards = boardRepository.findBoardsByMusicList(musicList);

        return boards.stream().map(board -> entityToGenreDto(board, getArtistMusicListDto(board)))
                .collect(Collectors.toList());
    }

    /// 메인 페이지까지 쓰는 부분

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

    //음악 추천 게시판 전체 리스트
    @Override
    public BoardListRequestDto.BoardListDto findBoardList() {
        BoardListRequestDto.BoardPopUpDto boardPopUpDto = findBoardPopUp();
        Page<BoardListRequestDto.BoardDto> boardDtos = findBoardDto(1);

        return BoardListRequestDto.BoardListDto.of(boardPopUpDto, boardDtos);
    }

    //음악 추천 게시판 리스트 부분
    @Override
    public Page<BoardListRequestDto.BoardDto> findBoardDto(int page) {
        if (page < 1) { // 잘못된 접근으로 throw할때 쿼리문 실행을 안하기 위해 나눠서 체크
            throw new CustomException(BAD_REQUEST_REPLY_PAGE);
        }

        Pageable pageable = PageRequest.of(page - 1, PAGESIZE);
        Page<Board> boards = boardRepository.findActiveBoardPage(pageable);

        int totalPages = boards.getTotalPages();
        if (page - 1 > totalPages) {
            throw new CustomException(BAD_REQUEST_REPLY_PAGE);
        }

        return boards.map(board -> {
            List<GenreDto> genreList = getGenreMusicListDto(board);
            List<MoodDto> moodList = getMoodMusicListDto(board);
            List<ArtistDto> artistList = getArtistMusicListDto(board);
            return BoardListRequestDto.BoardDto.toDto(board, genreList, moodList, artistList);
        });
    }

    // 검색조건으로 음악 추천 게시판 검색
    @Transactional
    @Override
    public Page<BoardListRequestDto.BoardDto> search(int page, String searchType, String keyword) {
        if (page < 1) { // 잘못된 접근으로 throw할때 쿼리문 실행을 안하기 위해 나눠서 체크
            throw new CustomException(BAD_REQUEST_REPLY_PAGE);
        }

        Pageable pageable = PageRequest.of(page - 1, PAGESIZE);

        Page<Board> boards = searchBoards(searchType, keyword, pageable);

        int totalPages = boards.getTotalPages();

        if (page - 1 > totalPages) {
            throw new CustomException(BAD_REQUEST_REPLY_PAGE);
        }

        return boards.map(board -> {
            List<GenreDto> genreList = getGenreMusicListDto(board);
            List<MoodDto> moodList = getMoodMusicListDto(board);
            List<ArtistDto> artistList = getArtistMusicListDto(board);
            return BoardListRequestDto.BoardDto.toDto(board, genreList, moodList, artistList);
        });
    }

    public void deleteBoard(Long boardId) {

        if (!boardRepository.existsById(boardId)) {
            throw new CustomException(NOT_FOUND_BOARDID, "Board does not exist");
        }
        boardRepository.deleteById(boardId);
    }

    @Transactional
    @Override
    public void updateBoard(Long boardId, UpdateBoardRequestDto updateRequest) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_BOARDID, "Board does not exist  id is" + boardId));

        List<MultipartFile> imgList = updateRequest.getImage();

        // 2. 업데이트 요청에 따라 필드 수정
        if (updateRequest.getTitle() != null) {
            board.builder().title(updateRequest.getTitle()).build();
        }

        if (updateRequest.getMusicTitle() != null) {
            board.getMusic().builder().name(updateRequest.getMusicTitle()).build();
        }
/*
        if (updateRequest.getArtist() != null) {
            board.getMusic().getArtist().builder().name(updateRequest.getArtist()).build();
        }*/
        if (updateRequest.getYoutubeLink() != null) {
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
        if (imgList != null) {
            for (MultipartFile file : imgList) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            }
        }

/*        if (updateRequest.getGenre() != null) {
            board.getMusic().builder().genre(updateRequest.getGenre());
        }*/
    }

    // 음악 추천 게시판 상세페이지 (리뷰 포함)
    @Override
    public BoardAndReplyPageDto findBoardDetailPage(long boardId) {
        BoardRequestDto.BoardDto boardDto = findBoard(boardId);
        Page<ReplyDto> replyDtos = replyService.findReplies(boardId, 1);

        return BoardAndReplyPageDto.of(boardDto, replyDtos);
    }


    //게시글 상세 정보
    private BoardRequestDto.BoardDto findBoard(long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new CustomException(NOT_FOUND_BOARDID));

/*        List<Artist> artistList = artist_musicRepository.findByMusic(board.getMusic()).stream()
                .map(Artist_Music::getArtist)
                .toList();
        List<Genre> genres = genre_musicRepository.findByMusic(board.getMusic()).stream()
                .map(Genre_Music::getGenre)
                .toList();
        List<Mood> moods = mood_musicRepository.findByMusic(board.getMusic()).stream()
                .map(Mood_Music::getMood)
                .toList();*/ // 동작 확인하고 지울 주석

        return BoardRequestDto.BoardDto.toDto(board, getArtistMusicListDto(board),
                getGenreMusicListDto(board), getMoodMusicListDto(board));
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

    // 음악 추천 게시판 상단
    private BoardListRequestDto.BoardPopUpDto findBoardPopUp() {
        Specification<Board> spec = Specification.where(BoardSpecificaion.isCreateAtAfterWeek())
                .and(BoardSpecificaion.isActiveCheckTrue()).and(BoardSpecificaion.findBoardsWithAtLeastTenRecommend());

        List<Board> boards = findBySpecBoard(spec);

        if (boards.isEmpty()) {
            return null;
        }

        // 조건에 맞는 3개 이하의 게시글 가져오기
        List<Board> randomBoard = selectRandomBoards(boards, 3);
        BoardListRequestDto.RecommendBoardFirstDto firstPopUpDto = findBoardListPopUpFirst(boards);

        List<BoardListRequestDto.RecommendBoardDto> boardPopUpDto = new ArrayList<>();

        boardPopUpDto.add(findBoardListPopUp(boards, 1));
        boardPopUpDto.add(findBoardListPopUp(boards, 2));

        return BoardListRequestDto.BoardPopUpDto.of(firstPopUpDto, boardPopUpDto);
    }

    private Page<Board> searchBoards(String searchType, String keyword, Pageable pageable) {
        switch (searchType) {
            case "username":
                return boardRepository.findActiveBoardsByUsername(keyword, pageable);
            case "title":
                return boardRepository.findActiveBoardsByTitle(keyword, pageable);
            case "artist":
                return boardRepository.findActiveBoardsByArtist(keyword, pageable);
            case "genre":
                return boardRepository.findActiveBoardsByGenre(keyword, pageable);
            case "mood":
                return boardRepository.findActiveBoardsByMood(keyword, pageable);
            default:
                throw new CustomException(NOT_FOUND_KEYWORD);
        }
    }

    private List<Board> findBySpecBoard(Specification<Board> spec) {
        return boardRepository.findAll(spec); //조건에 부합하는 게시글 전부가져오기
    }

    private List<Board> findBySpecBoard(Specification<Board> spec, PageRequest request) {
        Page<Board> boards = boardRepository.findAll(spec, request); //조건에 부합하는 게시글 전부가져오기
        return boards.getContent();
    }

    // 랜덤한 게시글을 가져오는 메서드
    private List<Board> selectRandomBoards(List<Board> boards, int count) {
        int effectiveCount = Math.min(count, boards.size());

        Set<Integer> selectedIndices = new HashSet<>();
        Random random = new Random();

        while (selectedIndices.size() < effectiveCount) {
            int randomIndex = random.nextInt(boards.size());
            selectedIndices.add(randomIndex);
        }

        return selectedIndices.stream()
                .map(boards::get)
                .collect(Collectors.toList());
    }

    private BoardListRequestDto.RecommendBoardFirstDto findBoardListPopUpFirst(List<Board> boards) {
        Board selectBoard = boards.get(0);
        return BoardListRequestDto.RecommendBoardFirstDto.toDto(selectBoard, getArtistMusicListDto(selectBoard));
    }

    private BoardListRequestDto.RecommendBoardDto findBoardListPopUp(List<Board> boards, int indexNum) {
        Board selectBoard = boards.get(indexNum);
        return BoardListRequestDto.RecommendBoardDto.toDto(selectBoard, getArtistMusicListDto(selectBoard));
    }

    private GenreBoardDto entityToGenreDto(Board board, List<ArtistDto> artists) { //장르로 검색한 게시글 엔티티를 Dto로 전환
        return GenreBoardDto.toDto(board, artists);
    }

    private RecommendBoardLeft entityToBoardDto(Board board) { //핫한 게시글을 엔티티에서 Dto로 전환
        return RecommendBoardLeft.toDto(board);
    }

    private RecommendBoardRight entityToMainDto(Board board) { //게시글을 엔티티에서 Dto로 전환
        return RecommendBoardRight.toDto(board);
    }
}


