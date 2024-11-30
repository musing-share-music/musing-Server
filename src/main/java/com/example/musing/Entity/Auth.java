package com.example.musing.Entity;

import jakarta.persistence.*;

@Entity
@Table(name="auth")
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String authname;

    @Column(nullable = false)
    private String desc;


}
