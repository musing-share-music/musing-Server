package com.example.musing.genre.entity;

import jakarta.persistence.*;
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


}
