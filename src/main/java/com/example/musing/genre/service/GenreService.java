package com.example.musing.genre.service;

import java.util.List;

public interface GenreService {
    String getRandomGenre();
    void modifyGenre_Music(long musicId, List<Long> genreList);
}
