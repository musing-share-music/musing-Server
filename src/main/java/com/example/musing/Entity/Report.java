package com.example.musing.Entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name="report")
public class Report {

    //신고 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Date reportedat;

    @Column(nullable = false,length = 500)
    private String content;
}
