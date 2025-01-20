package com.example.musing.user.service;

import com.example.musing.artist.repository.ArtistRepository;
import com.example.musing.exception.CustomException;
import com.example.musing.exception.ErrorCode;
import com.example.musing.genre.entity.Genre;
import com.example.musing.genre.entity.GerneEnum;
import com.example.musing.genre.repository.GenreRepository;
import com.example.musing.like_music.repository.Like_MusicRepository;
import com.example.musing.mood.repository.MoodRepository;
import com.example.musing.user.Dto.UserResponseDto;
import com.example.musing.user.entity.User;
import com.example.musing.user.entity.User_LikeArtist;
import com.example.musing.user.entity.User_LikeGenre;
import com.example.musing.user.entity.User_LikeMood;
import com.example.musing.user.repository.UserRepository;
import com.example.musing.user.repository.User_LikeArtistRepository;
import com.example.musing.user.repository.User_LikeGenreRepository;
import com.example.musing.user.repository.User_LikeMoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.musing.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final MoodRepository moodRepository;
    private final ArtistRepository artistRepository;
    private final Like_MusicRepository likeMusicRepository;
    private final User_LikeGenreRepository userLikeGenreRepository;
    private final User_LikeMoodRepository userLikeMoodRepository;
    private final User_LikeArtistRepository userLikeArtistRepository;

    @Override
    @Transactional
    public void saveGenres(String userId, List<Long> genres) {
        User user = findById(userId);
        genres.stream()
                .map(id -> genreRepository.findById(id)
                        .orElseThrow(() -> new CustomException(NOT_FOUND_GENRE)))
                .map(genre -> User_LikeGenre.of(genre, user))
                .forEach(userLikeGenreRepository::save);
    }
    @Override
    @Transactional
    public void saveMoods(String userId, List<Long> moods) {
        User user = findById(userId);
        moods.stream()
                .map(id -> moodRepository.findById(id)
                        .orElseThrow(() -> new CustomException(NOT_FOUND_MOOD)))
                .map(mood -> User_LikeMood.of(mood, user))
                .forEach(userLikeMoodRepository::save);
    }

    @Override
    @Transactional
    public void saveArtists(String userId, List<Long> artists) {
        User user = findById(userId);
        artists.stream()
                .map(id -> artistRepository.findById(id)
                        .orElseThrow(() -> new CustomException(NOT_FOUND_ARTIST)))
                .map(artist -> User_LikeArtist.of(artist, user))
                .forEach(userLikeArtistRepository::save);
    }

    @Override
    @Transactional
    public String checkInputTags(String userId) {
        User user = findById(userId);
        if(user.getActivated() == null){
            //장르, 분위기 ,아티스트 순서로 갈 예정
            if(!userLikeGenreRepository.existsByUser(user)){
                return "genre";
            }
            if (!userLikeMoodRepository.existsByUser(user)){
                return "mood";
            }else{
                return "artists";
            }
        }
        return "pass";
    }

    @Override
    public User findById(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }

    @Override
    public UserResponseDto.UserInfoDto getUserInfo(String userId){
        User user = findById(userId);
        return UserResponseDto.UserInfoDto.of(user, likeMusicCount(user), 0); //playList아직 없어서 0 임시값
    }

    private int likeMusicCount(User user){
        return likeMusicRepository.countByUser(user);
    }
}
