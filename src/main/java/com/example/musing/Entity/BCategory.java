package com.example.musing.Entity;

import jakarta.persistence.*;

@Entity
@Table(name="bcategory")
public class BCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = true)
    private String categoryname;

}
