package com.example.musing.genre.service;

import com.example.musing.genre.dto.GenreDto;

import java.util.List;

public interface GenreService {
    GenreDto getRandomGenre();
    void modifyGenre_Music(long musicId, List<Long> genreList);
}
