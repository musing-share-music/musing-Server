package com.example.musing.Entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
public class User {


    @Id
    @GeneratedValue
    @Column(name="userid")// AUTO_INCREMENT
    private Long id;


    @Column(nullable = false, length = 50)
    private String username; // 사용자 이름

    @Column(nullable = false, unique = true, length = 100)
    private String email; // 이메일 (유니크)

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> Boards = new ArrayList<>();

  //   @OneToMany(mappedBy = )
}
