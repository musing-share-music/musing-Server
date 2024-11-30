package com.example.musing.Entity;

import jakarta.persistence.*;

@Entity
@Table(name="Music")
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String artist;

    @Column(nullable = true)
    private String genre;

    @Column(nullable = false)
    private String playtime;

    @Column(nullable = true)
    private String album;

}
