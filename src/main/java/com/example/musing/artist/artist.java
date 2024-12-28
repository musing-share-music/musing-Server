package com.example.musing.artist;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter // Lombok 어노테이션 : 클래스 내 모든 필드의 Getter 메소드 자동 생성
@NoArgsConstructor // Lombok 어노테이션 : 기본 생성자 자동 추가
@Entity
@Table(name="artist")
public class artist {
    @Id
    private long id;
}
