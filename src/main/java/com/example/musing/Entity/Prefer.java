package com.example.musing.Entity;

import jakarta.persistence.*;

@Entity
@Table(name="prefer")
public class Prefer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String genre;
    @Column
    private String nation;
    @Column
    private String mood;
}
