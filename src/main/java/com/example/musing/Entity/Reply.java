package com.example.musing.Entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.TypeAlias;

@Entity
@Table(name="reply")
public class Reply {

    //댓글 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String content;

    @Column
    private String createdat;

    @Column
    private String updatedat;

}
