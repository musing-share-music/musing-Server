package com.example.musing.genre.repository;

import com.example.musing.genre.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    Genre findByGenreName(String genreName);
}
