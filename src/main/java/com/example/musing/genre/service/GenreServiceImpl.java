package com.example.musing.genre.service;

import com.example.musing.exception.CustomException;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.genre.entity.Genre;
import com.example.musing.genre.entity.Genre_Music;
import com.example.musing.genre.repository.GenreRepository;
import com.example.musing.genre.repository.Genre_MusicRepository;
import com.example.musing.music.entity.Music;
import com.example.musing.music.repository.MusicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

import static com.example.musing.exception.ErrorCode.NOT_FOUND_GENRE;
import static com.example.musing.exception.ErrorCode.NOT_FOUND_MUSIC;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;
    private final MusicRepository musicRepository;
    private final Genre_MusicRepository genreMusicRepository;

    @Override
    public GenreDto getRandomGenre() {
        Random random = new Random();
        long index = 0;
        if (genreRepository.count() != 0) {
            index = random.nextLong(genreRepository.count());
        }
        GenreDto genreMusicDto = GenreDto.toDto(genreRepository.findById(index).orElseThrow(() -> new CustomException(NOT_FOUND_GENRE)));
        return genreMusicDto;
    }

    // 수정하면서 태그를 바꿀때만 쓸수 있고 추가하는 부분은 따로 필요
    @Override
    public void modifyGenre_Music(long musicId, List<Long> genreList) {
        Music music = findMusic(musicId);

        for (Long genreId : genreList) {
            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new CustomException(NOT_FOUND_GENRE));

            createGenre_Music(music, genre);
        }
    }

    private Genre_Music createGenre_Music(Music music, Genre genre) {
        Genre_Music genreMusic = Genre_Music.of(music, genre);

        // music과 genre를 포함한 데이터가 있나 조회하고 없으면 save하도록 로직 추가하기
        // OneToMany를 사용한 컬렉션으로 조회하는것도 좋을듯함.
        // 이 검증 부분도 private 메서드로 하기

        genreMusicRepository.save(genreMusic);
        return genreMusic;
    }

    private Music findMusic(long musicId) {
        return musicRepository.findById(musicId).
                orElseThrow(() -> new CustomException(NOT_FOUND_MUSIC));
    }
}
