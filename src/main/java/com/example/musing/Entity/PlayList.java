package com.example.musing.Entity;

import jakarta.persistence.*;

import java.util.IdentityHashMap;

import static jakarta.persistence.GenerationType.*;

@Entity
@Table(name="playlist")
public class PlayList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String listname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    private User user; // 작성자
}
