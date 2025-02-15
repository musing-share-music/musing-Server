package com.example.musing.genre.entity;

import com.example.musing.genre.dto.GenreDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Table(name = "genre")
@Entity
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="genreid")
    private Long id;

    @Enumerated(EnumType.STRING)
    private GerneEnum genreName;

    @Builder
    public static GenreDto from(Genre genre) {
        return GenreDto.toDto(genre);
    }
}
