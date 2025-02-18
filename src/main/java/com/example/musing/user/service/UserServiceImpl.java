package com.example.musing.user.service;

import com.example.musing.artist.dto.ArtistDto;
import com.example.musing.artist.entity.Artist;
import com.example.musing.artist.repository.ArtistRepository;
import com.example.musing.board.dto.BoardListResponseDto;
import com.example.musing.board.entity.Board;
import com.example.musing.board.repository.BoardRepository;
import com.example.musing.exception.CustomException;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.genre.repository.GenreRepository;
import com.example.musing.like_music.repository.Like_MusicRepository;
import com.example.musing.mood.dto.MoodDto;
import com.example.musing.mood.repository.MoodRepository;
import com.example.musing.reply.dto.ReplyDto;
import com.example.musing.reply.repository.ReplyRepository;
import com.example.musing.user.dto.UserResponseDto;
import com.example.musing.user.entity.User;
import com.example.musing.user.entity.User_LikeArtist;
import com.example.musing.user.entity.User_LikeGenre;
import com.example.musing.user.entity.User_LikeMood;
import com.example.musing.user.repository.UserRepository;
import com.example.musing.user.repository.User_LikeArtistRepository;
import com.example.musing.user.repository.User_LikeGenreRepository;
import com.example.musing.user.repository.User_LikeMoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.musing.exception.ErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final MoodRepository moodRepository;
    private final ArtistRepository artistRepository;
    private final Like_MusicRepository likeMusicRepository;
    private final User_LikeGenreRepository userLikeGenreRepository;
    private final User_LikeMoodRepository userLikeMoodRepository;
    private final User_LikeArtistRepository userLikeArtistRepository;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;

    private static final Pageable PAGEABLE = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

    @Override
    @Transactional
    public List<GenreDto> updateGenres(User user, List<Long> chooseGenres) {
        List<User_LikeGenre> currentLikeGenres = userLikeGenreRepository.findByUser(user);

        List<User_LikeGenre> updatedLikeGenres = updateUserLikeGenres(user, currentLikeGenres, chooseGenres);

        return updatedLikeGenres.stream()
                .map(userLikeGenre -> GenreDto.toDto(userLikeGenre.getGenre()))
                .toList();
    }

    @Override
    @Transactional
    public List<MoodDto> updateMoods(User user, List<Long> chooseMoods) {
        List<User_LikeMood> currentLikeMoods = userLikeMoodRepository.findByUser(user);

        List<User_LikeMood> updatedLikeMoods = updateUserLikeMoods(user, currentLikeMoods, chooseMoods);

        return updatedLikeMoods.stream()
                .map(userLikeMood -> MoodDto.toDto(userLikeMood.getMood()))
                .toList();
    }

    @Override
    @Transactional
    public List<ArtistDto> updateArtists(User user, List<String> choosertists) {
        List<User_LikeArtist> currentLikeArtists = userLikeArtistRepository.findByUser(user);

        List<User_LikeArtist> updatedLikeArtists = updateUserLikeArtists(user, currentLikeArtists, choosertists);

        return updatedLikeArtists.stream()
                .map(userLikeArtist -> ArtistDto.toDto(userLikeArtist.getArtist()))
                .toList();
    }

    @Override
    public UserResponseDto.UserInfoPageDto getUserInfoPage(User user) {
        String userId = user.getId();

        List<GenreDto> likeGenres = getLikeGenres(userId);
        List<MoodDto> likeMoods = getLikeMoods(userId);
        List<ArtistDto> likeArtists = getLikeArtists(userId);

        List<BoardListResponseDto.BoardRecapDto> myBoard = getMyBoards(userId);

        List<ReplyDto> myReplies = getMyReplies(userId);

        return UserResponseDto.UserInfoPageDto.of(
                user, likeGenres, likeMoods, likeArtists, myBoard, myReplies);
    }

    @Override
    @Transactional
    public void saveGenres(String userId, List<Long> genres) {
        User user = findById(userId);
        List<User_LikeGenre> likeGenreList = genres.stream()
                .map(id -> genreRepository.findById(id)
                        .orElseThrow(() -> new CustomException(NOT_FOUND_GENRE)))
                .map(genre -> User_LikeGenre.of(genre, user))
                .toList();

        userLikeGenreRepository.saveAll(likeGenreList);
    }

    @Override
    @Transactional
    public void saveMoods(String userId, List<Long> moods) {
        User user = findById(userId);
        List<User_LikeMood> likeMoodList = moods.stream()
                .map(id -> moodRepository.findById(id)
                        .orElseThrow(() -> new CustomException(NOT_FOUND_MOOD)))
                .map(mood -> User_LikeMood.of(mood, user))
                .toList();

        userLikeMoodRepository.saveAll(likeMoodList);
    }

    @Override
    @Transactional
    public void saveArtists(String userId, List<String> artists) {
        User user = findById(userId);
        List<User_LikeArtist> likeArtistList = artists.stream()
                .map(name -> artistRepository.findByName(name)
                        .map(artist -> User_LikeArtist.of(artist, user))
                        .orElseGet(() -> {
                            Artist newArtist = artistRepository.save(Artist.of(name));
                            return User_LikeArtist.of(newArtist, user);
                        }))
                .toList();

        user.updateactivated(true);
        userLikeArtistRepository.saveAll(likeArtistList);
    }

    @Override
    @Transactional
    public String checkInputTags(String userId) {
        User user = findById(userId);
        System.out.println("check: " + user.getActivated());
        if (user.getActivated() != null) {
            return "pass";
        }
        //장르, 분위기 ,아티스트 순서로 갈 예정
        if (!userLikeGenreRepository.existsByUser(user)) {
            return "genre";
        }
        if (!userLikeMoodRepository.existsByUser(user)) {
            return "mood";
        }
        return "artists";
    }

    @Override
    public User findById(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }

    @Override
    public UserResponseDto.UserInfoDto getUserInfo(String userId) {
        User user = findById(userId);
        return UserResponseDto.UserInfoDto.of(user, likeMusicCount(user), 0); //playList아직 없어서 0 임시값
    }

    private List<User_LikeGenre> updateUserLikeGenres(User user, List<User_LikeGenre> currentLikeGenres, List<Long> chooseGenres) {
        Set<Long> chosenGenreIds = new HashSet<>(chooseGenres);

        currentLikeGenres.removeIf(likeGenre -> !chosenGenreIds.contains(likeGenre.getGenre().getId()));

        Set<Long> existingGenreIds = currentLikeGenres.stream()
                .map(likeGenre -> likeGenre.getGenre().getId())
                .collect(Collectors.toSet());

        List<User_LikeGenre> newLikeGenres = chooseGenres.stream()
                .filter(genreId -> !existingGenreIds.contains(genreId))
                .map(genreId -> genreRepository.findById(genreId)
                        .orElseThrow(() -> new CustomException(NOT_FOUND_GENRE)))
                .map(genre -> User_LikeGenre.of(genre, user))
                .toList();

        currentLikeGenres.addAll(newLikeGenres);

        return userLikeGenreRepository.saveAll(currentLikeGenres);
    }

    private List<User_LikeMood> updateUserLikeMoods(User user, List<User_LikeMood> currentLikeMoods, List<Long> chooseMoods) {
        Set<Long> chosenMoodIds = new HashSet<>(chooseMoods);

        currentLikeMoods.removeIf(likeMood -> !chosenMoodIds.contains(likeMood.getMood().getId()));

        Set<Long> existingMoodIds = currentLikeMoods.stream()
                .map(likeMood -> likeMood.getMood().getId())
                .collect(Collectors.toSet());

        List<User_LikeMood> newLikeMoods = chooseMoods.stream()
                .filter(moodId -> !existingMoodIds.contains(moodId))
                .map(moodId -> moodRepository.findById(moodId)
                        .orElseThrow(() -> new CustomException(NOT_FOUND_MOOD)))
                .map(mood -> User_LikeMood.of(mood, user))
                .toList();

        currentLikeMoods.addAll(newLikeMoods);

        return userLikeMoodRepository.saveAll(currentLikeMoods);
    }

    private List<User_LikeArtist> updateUserLikeArtists(User user, List<User_LikeArtist> currentLikeArtists, List<String> newArtists) {
        Set<String> chosenGenreIds = new HashSet<>(newArtists);

        currentLikeArtists.removeIf(likeArtist -> !chosenGenreIds.contains(likeArtist.getArtist().getName()));

        Set<String> currentArtistNames = currentLikeArtists.stream()
                .map(likeArtist -> likeArtist.getArtist().getName())
                .collect(Collectors.toSet());

        List<User_LikeArtist> newLikeArtists = newArtists.stream()
                .filter(name -> !currentArtistNames.contains(name))
                .map(name -> artistRepository.findByName(name)
                        .map(artist -> User_LikeArtist.of(artist, user))
                        .orElseGet(() -> {
                            Artist newArtist = artistRepository.save(Artist.of(name));
                            return User_LikeArtist.of(newArtist, user);
                        }))
                .toList();

        currentLikeArtists.addAll(newLikeArtists);
        return userLikeArtistRepository.saveAll(currentLikeArtists);
    }

    private List<BoardListResponseDto.BoardRecapDto> getMyBoards(String userId) {
        List<Board> myBoard = boardRepository.findActiveBoardsByUserId(userId, PAGEABLE);

        return myBoard.stream().map(BoardListResponseDto.BoardRecapDto::toDto)
                .toList();
    }

    private List<ReplyDto> getMyReplies(String userId) {
        return replyRepository.findByUserId(userId, PAGEABLE)
                .stream().map(ReplyDto::from)
                .toList();
    }

    private List<GenreDto> getLikeGenres(String userId) {
        return findById(userId).getGenres()
                .stream()
                .map(User_LikeGenre::getGenre)
                .map(GenreDto::toDto)
                .toList();
    }

    private List<MoodDto> getLikeMoods(String userId) {
        return findById(userId).getMoods()
                .stream()
                .map(User_LikeMood::getMood)
                .map(MoodDto::toDto)
                .toList();
    }

    private List<ArtistDto> getLikeArtists(String userId) {
        return findById(userId).getArtists()
                .stream()
                .map(User_LikeArtist::getArtist)
                .map(ArtistDto::toDto)
                .toList();
    }

    private int likeMusicCount(User user) {
        return likeMusicRepository.countByUser(user);
    }
}
