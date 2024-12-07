package com.example.musing.boardCategory.entity;

import com.example.musing.board.entity.Board;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter // Lombok 어노테이션 : 클래스 내 모든 필드의 Getter 메소드 자동 생성
@NoArgsConstructor // Lombok 어노테이션 : 기본 생성자 자동 추가
@Entity
@Table(name="bcategory")
public class BCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = true)
    private String categoryname;

    @OneToMany(mappedBy = "category" ,cascade = CascadeType.ALL, orphanRemoval = true )
    private List<Board> boards = new ArrayList<Board>();

}
