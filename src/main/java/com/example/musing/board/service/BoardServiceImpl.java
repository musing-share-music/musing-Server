package com.example.musing.board.service;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.artist.entity.Artist;
import com.example.musing.artist.entity.Artist_Music;
import com.example.musing.artist.repository.ArtistRepository;
import com.example.musing.artist.repository.Artist_MusicRepository;
import com.example.musing.board.dto.*;
import com.example.musing.board.entity.Board;
import com.example.musing.board.event.CommitState;
import com.example.musing.board.repository.BoardRepository;
import com.example.musing.common.utils.S3.AWS_S3_Util;
import com.example.musing.exception.CustomException;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.genre.entity.Genre;
import com.example.musing.genre.entity.Genre_Music;
import com.example.musing.genre.entity.GerneEnum;
import com.example.musing.genre.repository.GenreRepository;
import com.example.musing.genre.repository.Genre_MusicRepository;
import com.example.musing.like_music.entity.Like_Music;
import com.example.musing.like_music.repository.Like_MusicRepository;
import com.example.musing.like_music.service.Like_MusicService;
import com.example.musing.main.dto.RecommendBoardRight;
import com.example.musing.mood.dto.MoodDto;
import com.example.musing.mood.entity.MoodEnum;
import com.example.musing.mood.entity.Mood_Music;
import com.example.musing.music.entity.Music;
import com.example.musing.music.repository.MusicRepository;
import com.example.musing.reply.dto.ReplyResponseDto;
import com.example.musing.reply.service.ReplyService;
import com.example.musing.user.entity.User;
import com.example.musing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    private static int MAX_RETRY_COUNT =3;
    private static String S3BUCKETURL = "board";

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ArtistRepository artistRepository;
    private final Like_MusicRepository likeMusicRepository;
    private final MusicRepository musicRepository;
    private final GenreRepository genreRepository;
    private final Artist_MusicRepository artist_musicRepository;
    private final Genre_MusicRepository genre_musicRepository;
    private final ReplyService replyService;
    private final Like_MusicService likeMusicService;
    private final AWS_S3_Util awsS3Util;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateReplyState(Board board, Float oldRating, Float newRating, CommitState state) {
        switch (state) {
            case CREATE -> board.updateReplyStatsOnCreate(newRating);
            case DELETE -> board.updateReplyStatsOnDelete(newRating); // 삭제 시 newRating = deletedRating
            case UPDATE -> board.updateReplyStatsOnUpdate(oldRating, newRating);
            default -> throw new CustomException(ERROR);
        }
        boardRepository.save(board); // 변경 사항 저장
    }

    @Transactional
    @Override
    public void updateReplyStateWithRetry(Board board, Float oldRating, Float newRating, CommitState state) {
        int attempt = 0;

        while (attempt < MAX_RETRY_COUNT) {
            try {
                // 상태 업데이트 및 저장
                updateReplyState(board, oldRating, newRating, state);
                return; // 성공 시 메서드 종료

            } catch (OptimisticLockingFailureException e) {
                attempt++;
                if (attempt >= MAX_RETRY_COUNT) {
                    throw new CustomException(ERROR);
                }
            }
        }
    }

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
        Specification<Board> spec = Specification.where(BoardSpecificaion.isActiveCheckTrue())
                .and(BoardSpecificaion.orderByRecommendCountDesc());

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
        // Music 저장
        Music music = Music.of(request);
        musicRepository.save(music);

        // Artist 확인 및 저장, 중간 테이블 저장
        List<Artist_Music> artistMusics = new ArrayList<>();

        for (String artistName : request.getArtist()) {
            Optional<Artist> optionalArtist = artistRepository.findByName(artistName);

            if (optionalArtist.isEmpty()) { //해당 아티스트가 존재하지 않을 때
                Artist artist = Artist.of(artistName);
                artistRepository.save(artist);

                artistMusics.add(Artist_Music.of(
                        artist, music));
            } else {
                artistMusics.add(Artist_Music.of(
                        optionalArtist.get(), music)); //바로 불러와서 중간 테이블을 저장하기 위한 리스트에 적재
            }
        }

        artist_musicRepository.saveAll(artistMusics);

        //받은 장르의 Id리스트 여부 체크
        if (request.getGenre() == null || request.getGenre().isEmpty()) {
            throw new CustomException(NOT_FOUND_GENRE);
        }

        // 중간테이블 저장을 위한 테이블 선언
        List<Genre_Music> musicGenre = new ArrayList<>();

        // 장르 Id확인 후 저장을 위한 리스트 적재
        for (Long genreId : request.getGenre()) {
            Genre genre = genreRepository.findById(genreId).orElseThrow(() -> new CustomException(NOT_FOUND_GENRE));

            // Music과 Genre를 중간 테이블을 통해 연결
            musicGenre.add(Genre_Music.of(music, genre));
        }

        genre_musicRepository.saveAll(musicGenre); // Genre_Music 저장 (중간 테이블)

        User user = userRepository.findById(SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        String imageString = uploadImages(images) == null ? null : uploadImages(images).toString();

        Board board = Board.of(user, music, request.getTitle(), request.getContent(), imageString);
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
    public void updateBoard(Long boardId, UpdateBoardRequestDto request,
                            List<String> deleteFileLinks, List<MultipartFile> newFiles) {
        // 유저명 저장
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 기존 Board 객체를 찾기
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_BOARD));

        if (!Objects.equals(board.getUser().getId(), userId)) {
            throw new CustomException(NOT_MATCHED_BOARD_AND_USER);
        }

        // 이미지 가져오기
        List<String> images = board.getImageList();

        if (images == null) {
            images = new ArrayList<>();
        }

        if (deleteFileLinks != null && !deleteFileLinks.isEmpty()) {
            images.removeIf(imageUrl -> {
                if (deleteFileLinks.contains(imageUrl)) {
                    String filename = extractFilename(imageUrl);
                    awsS3Util.deleteFile(S3BUCKETURL, filename);
                    return true;
                }
                return false;
            });
        }

        if (newFiles != null && !newFiles.isEmpty()) {
            List<String> newImageUrls = uploadImages(newFiles);
            images.addAll(newImageUrls);
        }

        // Music 정보 업데이트
        Music music = board.getMusic().updateMusic(request);
        musicRepository.save(music);

        // 기존 Artist_Music 관계 삭제
        artist_musicRepository.deleteByMusic(music);

        // Artist 확인 및 저장, 중간 테이블 저장
        List<Artist_Music> artistMusics = new ArrayList<>();

        for (String artistName : request.getArtist()) {
            Optional<Artist> optionalArtist = artistRepository.findByName(artistName);

            if (optionalArtist.isEmpty()) { //해당 아티스트가 존재하지 않을 때
                Artist artist = Artist.of(artistName);
                artistRepository.save(artist);

                artistMusics.add(Artist_Music.of(
                        artist, music));
            } else {
                artistMusics.add(Artist_Music.of(
                        optionalArtist.get(), music)); //바로 불러와서 중간 테이블을 저장하기 위한 리스트에 적재
            }
        }

        artist_musicRepository.saveAll(artistMusics);

        // 기존 Gener_Music 관계 삭제
        genre_musicRepository.deleteByMusic(music);
        //받은 장르의 Id리스트 여부 체크
        if (request.getGenre() == null || request.getGenre().isEmpty()) {
            throw new CustomException(NOT_FOUND_GENRE);
        }

        // 중간테이블 저장을 위한 테이블 선언
        List<Genre_Music> musicGenre = new ArrayList<>();

        // 장르 Id확인 후 저장을 위한 리스트 적재
        for (Long genreId : request.getGenre()) {
            Genre genre = genreRepository.findById(genreId).orElseThrow(() -> new CustomException(NOT_FOUND_GENRE));

            // Music과 Genre를 중간 테이블을 통해 연결
            musicGenre.add(Genre_Music.of(music, genre));
        }

        genre_musicRepository.saveAll(musicGenre); // Genre_Music 저장 (중간 테이블)


        board.updateBoard(music, request.getTitle(), request.getContent(),
                images.isEmpty() ? null : images.toString());
    }

    @Transactional
    @Override
    public DetailResponse selectDetail(long boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new CustomException(NOT_FOUND_BOARD);
        }
        // 조회수 증가, DB 자체를 원자적으로 연산하도록 함.
        // 1차 캐시에 반영되지않기에 따로 findBy를 해줘야하기에 먼저 사용함
        incrementBoardViewCount(boardId);

        Board board = boardRepository.findBoardWithMusicAndArtist(boardId);

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
    @Transactional
    public BoardRecommedDto toggleLike(long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new CustomException(NOT_FOUND_BOARD));

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        boolean isLiked = likeMusicService.toggleRecommend(user, board.getMusic());
        int delta = isLiked ? 1 : -1;

        return BoardRecommedDto.of(boardRepository.updateRecommendCount(boardId, delta), isLiked);
    }


    // 음악 추천 게시판 상세페이지 (리뷰 포함)
    @Override
    public BoardAndReplyPageDto findBoardDetailPage(long boardId) {
        BoardRequestDto.BoardDto boardDto = findBoard(boardId);
        Page<ReplyResponseDto.ReplyDto> replyDtos = replyService.findReplies(boardId, 1, "date", "DESC");

        return BoardAndReplyPageDto.of(boardDto, replyDtos);
    }

    private void incrementBoardViewCount(long boardId) {
        boardRepository.incrementBoardViewCount(boardId);
    }

    private String extractFilename(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }

    private List<String> uploadImages(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return null;
        }

        List<String> urlList = new ArrayList<>();

        for (MultipartFile file : files) {
            // 이미지 파일명 생성
            UUID uuid = UUID.randomUUID();
            String fileName = uuid.toString() + "_" + file.getOriginalFilename().lastIndexOf(".");//uuid+확장자명으로 이름지정

            String imageUrl = awsS3Util.uploadImageToS3(file, S3BUCKETURL, fileName);//파일 업로드

            urlList.add(imageUrl);
        }
        return urlList;
    }

    private List<String> extractHashtags(String content) {
        if (content == null) return Collections.emptyList();
        return Arrays.stream(content.split("\\s+"))
                .filter(word -> word.startsWith("#"))
                .collect(Collectors.toList());
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
                .toList();
    }

    //게시글의 음악에 포함된 분위기를 Dto로 담아 리스트로 반환
    private List<MoodDto> getMoodMusicListDto(Board board) {
        return board.getMusic().getMoodMusics().stream()
                .map(Mood_Music::getMood)
                .map(MoodDto::toDto)
                .toList();
    }

    //게시글의 음악에 포함된 아티스트를 Dto로 담아 리스트로 반환
    private List<ArtistDto> getArtistMusicListDto(Board board) {
        return board.getMusic().getArtists().stream()
                .map(Artist_Music::getArtist)
                .map(ArtistDto::toDto)
                .toList();
    }

    // 음악 추천 게시판 상단
    private BoardListResponseDto.BoardPopUpDto findBoardPopUp() {
        Specification<Board> spec = Specification.where(BoardSpecificaion.isActiveCheckTrue())
                .and(BoardSpecificaion.findBoardsWithAtLeastTenRecommend());

        List<Board> boards = findBySpecBoard(spec);

        if (boards.isEmpty()) {
            return null;
        }

        // 조건에 맞는 3개 이하의 게시글 가져오기
        BoardListResponseDto.RecommendBoardFirstDto firstPopUpDto = findBoardListPopUpFirst(boards);

        List<BoardListResponseDto.BoardRecapDto> boardPopUpDto = new ArrayList<>();

        boardPopUpDto.add(findBoardListPopUp(boards, 1));
        boardPopUpDto.add(findBoardListPopUp(boards, 2));

        return BoardListResponseDto.BoardPopUpDto.of(firstPopUpDto, boardPopUpDto);
    }

    private Page<Board> searchBoards(String searchType, String keyword, Pageable pageable) {
        return switch (searchType) {
            case "username" -> boardRepository.findActiveBoardsByUsername(keyword, pageable);
            case "title" -> boardRepository.findActiveBoardsByTitle(keyword, pageable);
            case "artist" -> boardRepository.findActiveBoardsByArtist(keyword, pageable);
            case "genre" -> {
                GerneEnum gerneEnum = GerneEnum.fromKey(keyword);
                yield boardRepository.findActiveBoardsByGenre(gerneEnum, pageable);
            }
            case "mood" -> {
                MoodEnum moodEnum = MoodEnum.fromKey(keyword);
                yield boardRepository.findActiveBoardsByMood(moodEnum, pageable);
            }
            default -> throw new CustomException(NOT_FOUND_KEYWORD);
        };
    }

    private List<Board> findBySpecBoard(Specification<Board> spec) {
        return boardRepository.findAll(spec); //조건에 부합하는 게시글 전부가져오기
    }

    private List<Board> findBySpecBoard(Specification<Board> spec, PageRequest request) {
        Page<Board> boards = boardRepository.findAll(spec, request); //조건에 부합하는 게시글 전부가져오기
        return boards.getContent();
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


