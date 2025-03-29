package com.example.musing.alarm.entity;

import com.example.musing.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name="alarm")
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="alarmid")
    private long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String urlLink; //게시글 링크

    @Column(nullable = false)
    private boolean isRead;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private AlarmType alarmType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    @Builder
    public Alarm (String content, String urlLink, AlarmType alarmType, User user){
        this.content = content;
        this.urlLink = urlLink;
        this.alarmType = alarmType;
        this.user = user;
        this.isRead = false;
    }

    public static Alarm of(String content, String urlLink, AlarmType alarmType, User user) {
        return Alarm.builder()
                .content(content)
                .urlLink(urlLink)
                .alarmType(alarmType)
                .user(user)
                .build();
    }

    public void read(){
        isRead = true;
    }
}
