package com.example.musing.Entity;

import jakarta.persistence.*;

@Entity
@Table(name="hashtag")
public class HashTag {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String hashtag;




}
