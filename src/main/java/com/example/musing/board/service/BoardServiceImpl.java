package com.example.musing.board.service;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.artist.entity.Artist;
import com.example.musing.artist.entity.Artist_Music;
import com.example.musing.artist.repository.ArtistRepository;
import com.example.musing.artist.repository.Artist_MusicRepository;
import com.example.musing.board.dto.*;
import com.example.musing.board.entity.Board;
import com.example.musing.board.repository.BoardRepository;
import com.example.musing.exception.CustomException;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.genre.entity.Genre;
import com.example.musing.genre.entity.Genre_Music;
import com.example.musing.genre.entity.GerneEnum;
import com.example.musing.genre.repository.GenreRepository;
import com.example.musing.genre.repository.Genre_MusicRepository;
import com.example.musing.like_music.entity.Like_Music;
import com.example.musing.like_music.repository.Like_MusicRepository;
import com.example.musing.main.dto.RecommendBoardRight;
import com.example.musing.mood.dto.MoodDto;
import com.example.musing.mood.entity.MoodEnum;
import com.example.musing.mood.entity.Mood_Music;
import com.example.musing.mood.repository.Mood_MusicRepository;
import com.example.musing.music.entity.Music;
import com.example.musing.music.repository.MusicRepository;
import com.example.musing.reply.dto.ReplyRequestDto;
import com.example.musing.reply.dto.ReplyResponseDto;
import com.example.musing.reply.repository.ReplyRepository;
import com.example.musing.reply.service.ReplyService;
import com.example.musing.user.entity.User;
import com.example.musing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.musing.board.entity.CheckRegister.NEED_FIX;
import static com.example.musing.board.entity.CheckRegister.PERMIT;
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
    private final GenreRepository genreRepository;
    private final Artist_MusicRepository artist_musicRepository;
    private final Genre_MusicRepository genre_musicRepository;

    @Override
    public List<GenreBoardDto> findBy5GenreBoard(String genre) { //장르로 검색한 게시글들을 엔티티에서 Dto로 전환
        Specification<Board> spec = Specification.where(BoardSpecificaion.hasGenre(genre))
                .and(BoardSpecificaion.isActiveCheckTrue());

        List<Board> boards = findBySpecBoard(spec, pageRequestOrderBy);

        return boards.stream().map(this::entityToGenreDto)
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

        return boards.stream().map(this::entityToGenreDto)
                .collect(Collectors.toList());
    }

    /// 메인 페이지까지 쓰는 부분

    //게시판 등록 로직
    @Override
    @Transactional
    public void createBoard(CreateBoardRequest request, List<MultipartFile> images) {
        // 유저명 저장
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 파일명 리스트 생성
        List<String> fileNames = new ArrayList<>();
        if (images == null || images.isEmpty()) {
            fileNames.add("이미지 없음");
        } else {
            // 이미지가 있는 경우 파일명 생성
            for (MultipartFile file : images) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                // 파일 저장 로직 작성 필요함(서버나 S3에 저장)
                fileNames.add(fileName);
            }
        }

        // Artist 저장
        Artist artist = Artist.builder()
                .name(request.getArtist()) // request.getArtist()는 Artist의 이름(String)이라고 가정
                .build();

        // Music 저장
        Music music = Music.builder()
                .name(request.getMusicTitle())
                .playtime(request.getPlaytime())
                .albumName(request.getAlbumName())
                .songLink(request.getSongLink())
                .thumbNailLink(request.getThumbNailLink())
                .build();

        Artist_Music artistMusic = Artist_Music.of(artist, music);


        Long genreId = request.getGenre();
        if (genreId == null || genreId <= 0) {
            throw new CustomException(NOT_FOUND_GENRE);
        }


        Genre genre = genreRepository.findById(genreId).orElseThrow(() -> new CustomException(NOT_FOUND_GENRE));

        // Music과 Genre를 중간 테이블을 통해 연결
        Genre_Music musicGenre = Genre_Music.of(music, genre);

        artistRepository.save(artist); // Artist 먼저 저장
        musicRepository.save(music);


        genre_musicRepository.save(musicGenre); // Genre_Music 저장 (중간 테이블)
        artist_musicRepository.save(artistMusic);


        Long musicId = music.getId();
        if (musicId != null) {
            System.out.println("자동 생성된 Music ID: " + musicId);
        } else {
            System.out.println("Music ID가 자동으로 생성되지 않았습니다.");
        }


        User user = userRepository.findById(SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        // Board 저장
        Board board = Board.builder()
                .user(user)
                .music(music)
                .title(request.getTitle())
                .content(request.getContent())
                .image(String.join(",", fileNames)) // 파일명은 ,로 구분 지어 저장
                .activeCheck(false)
                .recommendCount(0)
                .viewCount(0)
                .build();
        boardRepository.save(board); // Board 저장 (최종적으로 Board를 저장)
    }


    //음악 추천 게시판 전체 리스트
    @Override
    public BoardListResponseDto.BoardListDto findBoardList() {
        BoardListResponseDto.BoardPopUpDto boardPopUpDto = findBoardPopUp();
        Page<BoardListResponseDto.BoardDto> boardDtos = findBoardDto(1);

        return BoardListResponseDto.BoardListDto.of(boardPopUpDto, boardDtos);
    }

    //음악 추천 게시판 리스트 부분
    @Override
    public Page<BoardListResponseDto.BoardDto> findBoardDto(int page) {
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
            return BoardListResponseDto.BoardDto.toDto(board, genreList, moodList, artistList);
        });
    }

    // 검색조건으로 음악 추천 게시판 검색
    @Override
    public Page<BoardListResponseDto.BoardDto> search(int page, String searchType, String keyword) {
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
            return BoardListResponseDto.BoardDto.toDto(board, genreList, moodList, artistList);
        });
    }

    @Transactional
    public void deleteBoard(Long boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new CustomException(NOT_FOUND_BOARD, "Board does not exist");
        }
        boardRepository.deleteById(boardId);
    }

    @Override
    @Transactional
    public void updateBoard(UpdateBoardRequestDto request, List<MultipartFile> images) {
        // 유저명 저장
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 기존 Board 객체를 찾기
        Board board = boardRepository.findById(request.getBoardId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_BOARD));

        // Board에 관련된 Music 객체를 찾기
        Music music = board.getMusic();

        // 파일명 리스트 생성
        List<String> fileNames = new ArrayList<>();
        if (images == null || images.isEmpty()) {
            fileNames.add("이미지 없음");
        } else {
            // 이미지가 있는 경우 파일명 생성
            for (MultipartFile file : images) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                // 파일 저장 로직 작성 필요함(서버나 S3에 저장)
                fileNames.add(fileName);
            }
        }

        // Artist 업데이트 또는 새로 생성
        Artist artist = artistRepository.findByName(request.getArtist())
                .orElseGet(() -> Artist.builder()
                        .name(request.getArtist())
                        .build());
        artistRepository.save(artist);

        // Music 정보 업데이트 (빌더 사용)
        music = Music.builder()// Music 객체를 빌더로 변환하여 업데이트
                .name(request.getMusicTitle())
                .playtime(request.getPlaytime())
                .songLink(request.getSongLink())
                .albumName(request.getAlbumName())
                .thumbNailLink(request.getThumbNailLink())
                .build();

        musicRepository.save(music);

        // 기존 중간 테이블에 저장된 Artist와 Music 관계 업데이트 (이전 관계를 삭제 후 새로 저장)
        artist_musicRepository.deleteByMusic(music); // 기존 관계 삭제
        Artist_Music artistMusic = Artist_Music.of(artist, music);
        artist_musicRepository.save(artistMusic); // 새 관계 저장

        // Genre 업데이트 (장르가 존재하면 이를 Music과 연결)
        Genre genre = genreRepository.getById(request.getGenre());

        // 기존 중간 테이블에 저장된 Genre와 Music 관계 업데이트 (이전 관계를 삭제 후 새로 저장)
        genre_musicRepository.deleteByMusic(music);// 기존 관계 삭제
        Genre_Music musicGenre = Genre_Music.of(music, genre);
        genre_musicRepository.save(musicGenre); // 새 관계 저장

        // Board 정보 업데이트 (빌더 사용)
        board = Board.builder() // Board 객체를 빌더로 변환하여 업데이트
                .user(userRepository.findById(userId)
                        .orElseThrow(() -> new CustomException(NOT_FOUND_USER)))
                .music(music)
                .title(request.getTitle())
                .content(request.getContent())
                .image(String.join(",", fileNames)) // 파일명은 ,로 구분 지어 저장
                .activeCheck(false) // 필요한 경우 수정 가능
                .recommendCount(board.getRecommendCount()) // 기존 추천 수 유지
                .viewCount(board.getViewCount()) // 기존 조회 수 유지
                .build();

        // Board 업데이트 저장
        boardRepository.save(board); // 영속화
    }

    public DetailResponse selectDetail(long boardId) {

        Board board = boardRepository.findBoardWithMusicAndArtist(boardId);
        if (!boardRepository.existsById(boardId)) {
            throw new CustomException(NOT_FOUND_BOARD);
        }

        // 첫 번째 Music 정보 가져오기
        Music music = board.getMusic();

        // 첫 번째 Music이 없으면 기본 값 설정
        String musicTitle = (music != null) ? music.getName() : null;
        String playtime = (music != null) ? music.getPlaytime() : null;
        String albumName = (music != null) ? music.getAlbumName() : null;
        String songLink = (music != null) ? music.getSongLink() : null;

        // 첫 번째 Music의 Artist 정보 가져오기
        String artistNames = (music != null && !music.getArtists().isEmpty())
                ? music.getArtists().stream()
                .map(am -> am.getArtist().getName())
                .collect(Collectors.joining(", ")) // 여러 명일 경우 쉼표로 구분
                : null;

        Long genreId = (music != null && !music.getArtists().isEmpty())
                ? music.getArtists().get(0).getArtist().getId() // 첫 번째 아티스트의 장르 ID 사용
                : null;

        return DetailResponse.builder()
                .title(board.getTitle())
                .musicTitle(musicTitle)
                .artist(artistNames)
                .youtubeLink(board.getMusic().getSongLink())
                .hashtags(extractHashtags(board.getContent())) // 해시태그 추출
                .genre(genreId)
                .content(board.getContent())
                .playtime(playtime)
                .AlbumName(albumName)
                .songLink(songLink)
                .thumbNailLink(board.getImage())
                .build();
    }

    private List<String> extractHashtags(String content) {
        if (content == null) return Collections.emptyList();
        return Arrays.stream(content.split("\\s+"))
                .filter(word -> word.startsWith("#"))
                .collect(Collectors.toList());
    }

    // 음악 추천 게시판 상세페이지 (리뷰 포함)
    @Override
    public BoardAndReplyPageDto findBoardDetailPage(long boardId) {
        BoardRequestDto.BoardDto boardDto = findBoard(boardId);
        Page<ReplyResponseDto.ReplyDto> replyDtos = replyService.findReplies(boardId, 1, "date", "DESC");

        return BoardAndReplyPageDto.of(boardDto, replyDtos);
    }


    //게시글 상세 정보
    private BoardRequestDto.BoardDto findBoard(long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new CustomException(NOT_FOUND_BOARD));

        return BoardRequestDto.BoardDto.toDto(board, getGenreMusicListDto(board), getMoodMusicListDto(board));
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
    private BoardListResponseDto.BoardPopUpDto findBoardPopUp() {
        Specification<Board> spec = Specification.where(BoardSpecificaion.isCreateAtAfterMonth())
                .and(BoardSpecificaion.isActiveCheckTrue()).and(BoardSpecificaion.findBoardsWithAtLeastTenRecommend());

        List<Board> boards = findBySpecBoard(spec);

        if (boards.isEmpty()) {
            return null;
        }

        // 조건에 맞는 3개 이하의 게시글 가져오기
        List<Board> randomBoard = selectRandomBoards(boards, 3);
        BoardListResponseDto.RecommendBoardFirstDto firstPopUpDto = findBoardListPopUpFirst(boards);

        List<BoardListResponseDto.BoardRecapDto> boardPopUpDto = new ArrayList<>();

        boardPopUpDto.add(findBoardListPopUp(boards, 1));
        boardPopUpDto.add(findBoardListPopUp(boards, 2));

        return BoardListResponseDto.BoardPopUpDto.of(firstPopUpDto, boardPopUpDto);
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
                GerneEnum gerneEnum = GerneEnum.fromKey(keyword);
                return boardRepository.findActiveBoardsByGenre(gerneEnum, pageable);
            case "mood":
                MoodEnum moodEnum = MoodEnum.fromKey(keyword);
                return boardRepository.findActiveBoardsByMood(moodEnum, pageable);
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

    private BoardListResponseDto.RecommendBoardFirstDto findBoardListPopUpFirst(List<Board> boards) {
        Board selectBoard = boards.get(0);
        return BoardListResponseDto.RecommendBoardFirstDto.toDto(selectBoard, getArtistMusicListDto(selectBoard));
    }

    private BoardListResponseDto.BoardRecapDto findBoardListPopUp(List<Board> boards, int indexNum) {
        Board selectBoard = boards.get(indexNum);
        return BoardListResponseDto.BoardRecapDto.toDto(selectBoard);
    }

    private GenreBoardDto entityToGenreDto(Board board) { //장르로 검색한 게시글 엔티티를 Dto로 전환
        return GenreBoardDto.toDto(board);
    }

    private RecommendBoardLeft entityToBoardDto(Board board) { //핫한 게시글을 엔티티에서 Dto로 전환
        return RecommendBoardLeft.toDto(board);
    }

    private RecommendBoardRight entityToMainDto(Board board) { //게시글을 엔티티에서 Dto로 전환
        return RecommendBoardRight.toDto(board);
    }
}


